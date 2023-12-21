package io.github.drakonkinst.worldsinger.config;

import io.github.drakonkinst.worldsinger.compat.Compat;
import io.github.drakonkinst.worldsinger.compat.Compat.Mod;
import io.github.drakonkinst.worldsinger.compat.yacl.ModYACLConfigScreen;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import java.net.URI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

// Optional dependency on YACL. If it does not exist, you are prompted to install it.
public class ModConfigScreen {

    private static final String YACL_URL = "https://modrinth.com/mod/yacl/versions";

    public static Screen create(Screen parent) {
        if (!Compat.isModLoaded(Mod.YACL)) {
            return new ConfirmScreen((result) -> {
                if (result) {
                    Util.getOperatingSystem().open(URI.create(YACL_URL));
                }
                MinecraftClient.getInstance().setScreen(parent);
            }, ModConfigScreen.getConfigText("missing"),
                    ModConfigScreen.getConfigText("missing.message"), ScreenTexts.YES,
                    ScreenTexts.NO);
        }

        if (!Compat.isModUpToDate(Mod.YACL)) {
            return new ConfirmScreen((result) -> {
                if (result) {
                    Util.getOperatingSystem().open(URI.create(YACL_URL));
                }
                MinecraftClient.getInstance().setScreen(parent);
            }, ModConfigScreen.getConfigText("outdated"),
                    ModConfigScreen.getConfigText("outdated.message"), ScreenTexts.YES,
                    ScreenTexts.NO);
        }

        return ModYACLConfigScreen.generateConfigScreen(parent);
    }

    private static Text getConfigText(String key) {
        return Text.translatable("config." + ModConstants.MOD_ID + ".yacl." + key);
    }
}
