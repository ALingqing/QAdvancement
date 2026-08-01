package icu.epochcraft.client;

import icu.epochcraft.common.Networking;
import icu.epochcraft.common.AchievementEditorScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;

public class QAdvancementClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Networking.registerClient();
        ScreenRegistry.<AchievementEditorScreenHandler, AchievementEditorScreen>register(AchievementEditorScreenHandler.TYPE, AchievementEditorScreen::new);
    }

    public static void openScreen(String[] ids, String[] titles, String[] descriptions, String[] categories, String[] icons, boolean[] completed) {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen screen = new QAdvancementScreen(ids, titles, descriptions, categories, icons, completed, new LiteralText("QAdvancement 成就"));
        client.setScreen(screen);
    }
}