package io.github.drakonkinst.worldsinger.compat.yacl;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModYACLConfigScreen {

    public static Screen generateConfigScreen(Screen parent) {
        // TODO Hardcoding strings so I know to come back and replace these with translations later
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("WORK IN PROGRESS"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("WORK IN PROGRESS CATEGORY"))
                        .build())
                .save(() -> {
                    // Save function
                })
                .build()
                .generateScreen(parent);
    }
}
