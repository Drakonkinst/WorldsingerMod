package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.util.Constants;
import io.github.drakonkinst.worldsinger.util.DataTable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class ModRegistries {

    public static final RegistryKey<Registry<DataTable>> DATA_TABLES = RegistryKey.ofRegistry(
            new Identifier(
                    Constants.MOD_ID, "data_tables"));

    private ModRegistries() {}

    public static void register() {
        DataTables.register();
    }
}
