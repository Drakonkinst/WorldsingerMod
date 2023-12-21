package io.github.drakonkinst.worldsinger.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

// Thanks, Bawnorton!
public class Compat {

    private static Version parseVersion(String versionStr) {
        try {
            return Version.parse(versionStr);
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    public enum Mod {
        YACL("yet_another_config_lib_v3", "3.1.1");

        private final String id;
        private final Version minVersion;

        Mod(String id, String minVersionStr) {
            this.id = id;
            this.minVersion = Compat.parseVersion(minVersionStr);
        }

        public String getId() {
            return id;
        }

        public Version getMinVersion() {
            return minVersion;
        }
    }

    public static boolean isModLoaded(Mod mod) {
        return FabricLoader.getInstance().isModLoaded(mod.getId());
    }

    public static boolean isModUpToDate(Mod mod) {
        return FabricLoader.getInstance()
                .getModContainer(mod.getId())
                .filter(modContainer ->
                        modContainer.getMetadata().getVersion().compareTo(mod.getMinVersion()) >= 0)
                .isPresent();
    }
}
