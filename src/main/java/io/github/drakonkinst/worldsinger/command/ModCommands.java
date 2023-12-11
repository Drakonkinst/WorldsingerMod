package io.github.drakonkinst.worldsinger.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public final class ModCommands {

    public static final int PERMISSION_LEVEL_GAMEMASTER = 2;

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> ModCommands.createCommands(
                        dispatcher));
    }

    private static void createCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        SeetheCommand.register(dispatcher);
        TableCommand.register(dispatcher);
        SporeCommand.register(dispatcher);
    }

    private ModCommands() {}
}
