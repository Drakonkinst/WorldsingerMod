package io.github.drakonkinst.worldsinger.command;

import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.command.argument.Vec3ArgumentType.getVec3;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticleManager;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class SporeCommand {

    private static final AetherSporeType[] VALUES = AetherSporeType.values();

    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(
            Arrays.stream(VALUES).map(AetherSporeType::asString),
            builder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("spore")
                .requires(source -> source.hasPermissionLevel(
                        ModCommands.PERMISSION_LEVEL_GAMEMASTER))
                .then(argument("spore_type", StringArgumentType.word())
                        .suggests(SUGGESTION_PROVIDER)
                        .then(argument("pos", Vec3ArgumentType.vec3())
                                .then(argument("horizontal_radius",
                                        DoubleArgumentType.doubleArg(0.0))
                                        .then(argument("height", DoubleArgumentType.doubleArg(0.0))
                                                .then(argument("size",
                                                        FloatArgumentType.floatArg(0.0f))
                                                        .then(argument("count",
                                                                IntegerArgumentType.integer(1))
                                                                .executes(
                                                                        SporeCommand::spawnSporeParticle))))))));
    }

    public static int spawnSporeParticle(CommandContext<ServerCommandSource> context) {
        String aetherSporeTypeStr = getString(context, "spore_type");
        Optional<AetherSporeType> aetherSporeType = getAetherSporeTypeFromString(
                aetherSporeTypeStr);
        if (aetherSporeType.isEmpty()) {
            context.getSource().sendError(
                    Text.literal("Unknown aether spore type \"" + aetherSporeTypeStr + "\""));
            return 0;
        }
        Vec3d pos = getVec3(context, "pos");
        double horizontalRadius = getDouble(context, "horizontal_radius");
        double height = getDouble(context, "height");
        float size = getFloat(context, "size");
        int count = getInteger(context, "count");
        SporeParticleManager.createSporeParticles(context.getSource().getWorld(),
                aetherSporeType.get(), pos.x, pos.y, pos.z, horizontalRadius, height, size, count,
                false);
        context.getSource().sendFeedback(() -> Text.literal(
                "Spawned aether spore particle of type " + aetherSporeType.get().asString()), true);
        return 1;
    }

    private static Optional<AetherSporeType> getAetherSporeTypeFromString(String str) {
        for (AetherSporeType aetherSporeType : VALUES) {
            if (aetherSporeType.asString().equals(str)) {
                return Optional.of(aetherSporeType);
            }
        }
        return Optional.empty();
    }
}
