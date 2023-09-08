package io.github.drakonkinst.worldsinger.datatable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class DataTable {

    private final DataTableType type;
    private final Object2IntMap<Identifier> entryTable;
    private Object2IntMap<Identifier> unresolvedTags;

    public DataTable(DataTableType type, int defaultValue, Object2IntMap<Identifier> entryTable,
            Object2IntMap<Identifier> unresolvedTags) {
        this.type = type;
        this.entryTable = entryTable;
        this.unresolvedTags = unresolvedTags;
        this.entryTable.defaultReturnValue(defaultValue);
    }

    public void merge(Object2IntMap<Identifier> otherEntryTable,
            Object2IntMap<Identifier> otherUnresolvedTags) {
        for (Object2IntMap.Entry<Identifier> entry : otherEntryTable.object2IntEntrySet()) {
            entryTable.put(entry.getKey(), entry.getIntValue());
        }
        if (otherUnresolvedTags != null) {
            if (unresolvedTags == null) {
                unresolvedTags = otherUnresolvedTags;
            } else {
                unresolvedTags.putAll(otherUnresolvedTags);
            }
        }
    }

    public List<Identifier> resolveTags() {
        if (unresolvedTags == null || unresolvedTags.isEmpty()) {
            return null;
        }

        if (type == DataTableType.BLOCK) {
            return resolveBlockTags();
        }
        if (type == DataTableType.ENTITY) {
            return resolveEntityTypeTags();
        }
        if (type == DataTableType.ITEM) {
            return resolveItemTags();
        }
        return null;
    }

    private List<Identifier> resolveBlockTags() {
        Registries.BLOCK.streamTagsAndEntries().forEach(tagKeyNamedPair -> {
            Identifier tagId = tagKeyNamedPair.getFirst().id();
            if (!unresolvedTags.containsKey(tagId)) {
                return;
            }

            int value = unresolvedTags.getInt(tagId);
            tagKeyNamedPair.getSecond().forEach(blockRegistryEntry -> {
                Identifier blockId = Registries.BLOCK.getId(blockRegistryEntry.value());
                entryTable.putIfAbsent(blockId, value);
            });
            unresolvedTags.removeInt(tagId);
        });

        List<Identifier> failedTags = null;
        if (!unresolvedTags.isEmpty()) {
            failedTags = unresolvedTags.keySet().stream().toList();
        }
        unresolvedTags = null;
        return failedTags;
    }

    private List<Identifier> resolveEntityTypeTags() {
        Registries.ENTITY_TYPE.streamTagsAndEntries().forEach(tagKeyNamedPair -> {
            Identifier tagId = tagKeyNamedPair.getFirst().id();
            if (!unresolvedTags.containsKey(tagId)) {
                return;
            }

            int value = unresolvedTags.getInt(tagId);
            tagKeyNamedPair.getSecond().forEach(blockRegistryEntry -> {
                Identifier entityId = Registries.ENTITY_TYPE.getId(blockRegistryEntry.value());
                entryTable.putIfAbsent(entityId, value);
            });
            unresolvedTags.removeInt(tagId);
        });

        List<Identifier> failedTags = null;
        if (!unresolvedTags.isEmpty()) {
            failedTags = unresolvedTags.keySet().stream().toList();
        }
        unresolvedTags = null;
        return failedTags;
    }

    private List<Identifier> resolveItemTags() {
        Registries.ITEM.streamTagsAndEntries().forEach(tagKeyNamedPair -> {
            Identifier tagId = tagKeyNamedPair.getFirst().id();
            if (!unresolvedTags.containsKey(tagId)) {
                return;
            }

            int value = unresolvedTags.getInt(tagId);
            tagKeyNamedPair.getSecond().forEach(blockRegistryEntry -> {
                Identifier itemId = Registries.ITEM.getId(blockRegistryEntry.value());
                entryTable.putIfAbsent(itemId, value);
            });
            unresolvedTags.removeInt(tagId);
        });

        List<Identifier> failedTags = null;
        if (!unresolvedTags.isEmpty()) {
            failedTags = unresolvedTags.keySet().stream().toList();
        }
        unresolvedTags = null;
        return failedTags;
    }

    public int getInt(Identifier id) {
        return entryTable.getInt(id);
    }

    public int getIntForBlock(BlockState blockState) {
        Identifier id = Registries.BLOCK.getId(blockState.getBlock());
        return this.getInt(id);
    }

    public int getIntForEntity(Entity entity) {
        Identifier id = EntityType.getId(entity.getType());
        return this.getInt(id);
    }

    public int getIntForItem(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        return this.getInt(id);
    }

    public DataTableType getType() {
        return type;
    }
}

