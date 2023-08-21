package io.github.drakonkinst.worldsinger.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.Codecs.TagEntryId;

public class DataTable {

    private static final Codec<Map<TagEntryId, Integer>> TABLE_CODEC = Codec.unboundedMap(
            Codecs.TAG_ENTRY_ID, Codec.INT);
    private static final Codec<DataTableFile> FILE_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    TABLE_CODEC.fieldOf("entries").forGetter(DataTableFile::table),
                    Codec.INT.optionalFieldOf("default", 1)
                            .forGetter(DataTableFile::defaultValue),
                    Codec.BOOL.optionalFieldOf("replace", false)
                            .forGetter(DataTableFile::replace)
            ).apply(instance, DataTableFile::new));
    public static final Codec<DataTable> CODEC = FILE_CODEC.flatComapMap(DataTable::fromFile,
            dataTable -> DataResult.error(
                    () -> "Cannot convert from data table to data table file"));

    public record DataTableFile(Map<TagEntryId, Integer> table, int defaultValue,
                                boolean replace) {}

    public static DataTable fromFile(DataTableFile dataTableFile) {
        Object2IntMap<Identifier> idTable = new Object2IntArrayMap<>();
        Object2IntMap<Identifier> tagTable = new Object2IntArrayMap<>();
        for (Map.Entry<TagEntryId, Integer> entry : dataTableFile.table.entrySet()) {
            TagEntryId tagEntryId = entry.getKey();
            int value = entry.getValue();
            if (tagEntryId.tag()) {
                tagTable.put(tagEntryId.id(), value);
            } else {
                idTable.put(tagEntryId.id(), value);
            }
        }
        return new DataTable(dataTableFile.defaultValue, idTable, tagTable);
    }

    private final int defaultValue;
    private final Object2IntMap<Identifier> idTable;
    private final Object2IntMap<Identifier> tagTable;

    private DataTable(int defaultValue, Object2IntMap<Identifier> idTable,
            Object2IntMap<Identifier> tagTable) {
        this.defaultValue = defaultValue;
        this.idTable = idTable;
        this.tagTable = tagTable;
    }

    public int getIntForBlock(BlockState blockState) {
        Identifier id = Registries.BLOCK.getId(blockState.getBlock());

        if (idTable.containsKey(id)) {
            return idTable.getInt(id);
        }

        List<Identifier> blockTags = blockState.streamTags().map(TagKey::id).toList();
        for (Identifier blockTag : blockTags) {
            if (tagTable.containsKey(blockTag)) {
                return tagTable.getInt(blockTag);
            }
        }

        return defaultValue;
    }
}
