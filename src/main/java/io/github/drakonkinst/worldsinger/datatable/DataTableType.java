package io.github.drakonkinst.worldsinger.datatable;

public enum DataTableType {
    BLOCK,
    ENTITY,
    ITEM,
    MISC;

    private final String name;

    DataTableType() {
        this.name = name().toLowerCase();
    }

    String getName() {
        return name;
    }
}
