package io.github.drakonkinst.worldsinger.command;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SeetheComponent;
import io.github.drakonkinst.worldsinger.util.Constants;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class SeetheCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("seethe")
                .requires(source -> source.hasPermissionLevel(
                        ModCommands.PERMISSION_LEVEL_GAMEMASTER))
                .then(literal("on")
                        .executes(SeetheCommand::activateNoArgs)
                        .then(argument("duration", TimeArgumentType.time(0))
                                .executes(SeetheCommand::activateWithArgs)))
                .then(literal("off")
                        .executes(SeetheCommand::deactivateNoArgs)
                        .then(argument("duration", TimeArgumentType.time(0))
                                .executes(SeetheCommand::deactivateWithArgs)))
                .executes(SeetheCommand::getStatus));
    }

    private static SeetheComponent getSeethe(CommandContext<ServerCommandSource> context) {
        return ModComponents.LUMAR_SEETHE.get(context.getSource().getServer().getScoreboard());
    }

    private static int getStatus(CommandContext<ServerCommandSource> context) {
        SeetheComponent seethe = SeetheCommand.getSeethe(context);
        boolean isSeething = seethe.isSeething();
        int cycleTicks = seethe.getTicksUntilNextCycle();
        context.getSource().sendMessage(Text.literal(
                "Seethe is " + (isSeething ? "ACTIVE" : "INACTIVE") + " for the next " + cycleTicks
                        + " ticks, or " + (MathHelper.floor(
                        cycleTicks * Constants.TICKS_TO_SECONDS)) + " seconds"));
        ModComponents.LUMAR_SEETHE.sync(context.getSource().getServer().getScoreboard());
        return 1;
    }

    private static int activateNoArgs(CommandContext<ServerCommandSource> context) {
        SeetheComponent seethe = SeetheCommand.getSeethe(context);
        seethe.startSeethe();
        context.getSource().sendMessage(Text.literal("Set seethe to ACTIVE"));
        ModComponents.LUMAR_SEETHE.sync(context.getSource().getServer().getScoreboard());
        return 1;
    }

    private static int deactivateNoArgs(CommandContext<ServerCommandSource> context) {
        SeetheComponent seethe = SeetheCommand.getSeethe(context);
        seethe.stopSeethe();
        context.getSource().sendMessage(Text.literal("Set seethe to INACTIVE"));
        ModComponents.LUMAR_SEETHE.sync(context.getSource().getServer().getScoreboard());
        return 1;
    }

    private static int activateWithArgs(CommandContext<ServerCommandSource> context) {
        SeetheComponent seethe = SeetheCommand.getSeethe(context);
        seethe.startSeethe(getInteger(context, "duration"));
        context.getSource().sendMessage(Text.literal("Set seethe to ACTIVE"));
        ModComponents.LUMAR_SEETHE.sync(context.getSource().getServer().getScoreboard());
        return 1;
    }

    private static int deactivateWithArgs(CommandContext<ServerCommandSource> context) {
        SeetheComponent seethe = SeetheCommand.getSeethe(context);
        seethe.stopSeethe(getInteger(context, "duration"));
        context.getSource().sendMessage(Text.literal("Set seethe to INACTIVE"));
        ModComponents.LUMAR_SEETHE.sync(context.getSource().getServer().getScoreboard());
        return 1;
    }
}
