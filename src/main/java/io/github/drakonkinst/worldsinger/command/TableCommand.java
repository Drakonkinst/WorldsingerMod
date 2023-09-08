package io.github.drakonkinst.worldsinger.command;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.drakonkinst.worldsinger.datatable.DataTable;
import io.github.drakonkinst.worldsinger.datatable.DataTableRegistry;
import io.github.drakonkinst.worldsinger.datatable.DataTables;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class TableCommand {

    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        Collection<Identifier> dataTableIds = DataTableRegistry.INSTANCE.getDataTableIds();
        return CommandSource.suggestIdentifiers(dataTableIds.stream(), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("table")
                .requires(source -> source.hasPermissionLevel(
                        ModCommands.PERMISSION_LEVEL_GAMEMASTER))
                .then(literal("list")
                        .executes(TableCommand::listTables))
                .then(literal("get")
                        .then(argument("data_table_id",
                                IdentifierArgumentType.identifier())
                                .suggests(SUGGESTION_PROVIDER)
                                .then(literal("block")
                                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                                .executes(
                                                        TableCommand::getTableEntryForBlock))))));
    }

    private static int listTables(CommandContext<ServerCommandSource> context) {
        Collection<Identifier> dataTableIds = DataTableRegistry.INSTANCE.getDataTableIds();
        StringBuilder str = new StringBuilder(
                "There are " + dataTableIds.size() + " data tables currently loaded:");
        for (Identifier id : dataTableIds) {
            str.append(", ").append(id.toString());
        }
        context.getSource().sendMessage(Text.literal(str.toString()));
        return 1;
    }

    private static int getTableEntryForBlock(CommandContext<ServerCommandSource> context) {
        BlockPos blockPos = null;
        try {
            blockPos = BlockPosArgumentType.getLoadedBlockPos(context, "pos");
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Text.literal("Coordinates must be in a loaded position"));
        }
        Identifier id = IdentifierArgumentType.getIdentifier(context, "data_table_id");
        Optional<DataTable> table = DataTables.getOptional(id);
        if (table.isEmpty()) {
            context.getSource()
                    .sendError(Text.literal("Table " + id.toString() + " does not exist"));
            return 0;
        }
        int value = table.get()
                .getIntForBlock(context.getSource().getWorld().getBlockState(blockPos));
        context.getSource().sendMessage(Text.literal("Returned value of " + value));
        return value;
    }
}
