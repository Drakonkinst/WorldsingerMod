package io.github.drakonkinst.worldsinger.command;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.drakonkinst.worldsinger.util.Constants;
import io.github.drakonkinst.worldsinger.world.LumarSeetheAccess;
import io.github.drakonkinst.worldsinger.world.LumarSeetheData;
import io.github.drakonkinst.worldsinger.world.LumarSeetheManager;
import io.github.drakonkinst.worldsinger.world.LumarSeetheManagerAccess;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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

    private static int getStatus(CommandContext<ServerCommandSource> context) {
        LumarSeetheData lumarSeetheData = ((LumarSeetheAccess) context.getSource()
                .getWorld()).worldsinger$getLumarSeetheData();
        boolean isSeething = lumarSeetheData.isSeething();
        int cycleTicks = lumarSeetheData.getCycleTicks();
        int cyclesUntilNextLongStilling = lumarSeetheData.getCyclesUntilNextLongStilling();
        context.getSource().sendMessage(Text.literal(
                "Seethe is " + (isSeething ? "ACTIVE" : "INACTIVE") + " for the next " + cycleTicks
                        + " ticks, or " + ((int) Math.floor(
                        cycleTicks * Constants.TICKS_TO_SECONDS))
                        + " seconds\nThere are " + cyclesUntilNextLongStilling
                        + " cycles until the next long stilling"));
        return 1;
    }

    private static int activateNoArgs(CommandContext<ServerCommandSource> context) {
        LumarSeetheManager lumarSeetheManager = ((LumarSeetheManagerAccess) context.getSource()
                .getWorld()).worldsinger$getLumarSeetheManager();
        lumarSeetheManager.startSeething();
        context.getSource().sendMessage(Text.literal("Set seethe to ACTIVE"));
        return 1;
    }

    private static int deactivateNoArgs(CommandContext<ServerCommandSource> context) {
        LumarSeetheManager lumarSeetheManager = ((LumarSeetheManagerAccess) context.getSource()
                .getWorld()).worldsinger$getLumarSeetheManager();
        lumarSeetheManager.startStilling();
        context.getSource().sendMessage(Text.literal("Set seethe to INACTIVE"));
        return 1;
    }

    private static int activateWithArgs(CommandContext<ServerCommandSource> context) {
        LumarSeetheManager lumarSeetheManager = ((LumarSeetheManagerAccess) context.getSource()
                .getWorld()).worldsinger$getLumarSeetheManager();
        lumarSeetheManager.startSeething(getInteger(context, "duration"));
        context.getSource().sendMessage(Text.literal("Set seethe to ACTIVE"));
        return 1;
    }

    private static int deactivateWithArgs(CommandContext<ServerCommandSource> context) {
        LumarSeetheManager lumarSeetheManager = ((LumarSeetheManagerAccess) context.getSource()
                .getWorld()).worldsinger$getLumarSeetheManager();
        lumarSeetheManager.startStilling(getInteger(context, "duration"));
        context.getSource().sendMessage(Text.literal("Set seethe to INACTIVE"));
        return 1;
    }
}
