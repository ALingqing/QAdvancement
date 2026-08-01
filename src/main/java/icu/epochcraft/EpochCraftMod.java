package icu.epochcraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import icu.epochcraft.common.AchievementEditorScreenHandler;
import icu.epochcraft.common.AchievementEvents;
import icu.epochcraft.common.AchievementManager;
import icu.epochcraft.common.QAdvancementItems;

public class EpochCraftMod implements ModInitializer {
    public static final String MODID = "qadvancement";

    @Override
    public void onInitialize() {
        AchievementManager.init();
        AchievementEvents.register();
        QAdvancementItems.register();
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(MODID, "achievement_editor"), AchievementEditorScreenHandler.TYPE);
        ServerLifecycleEvents.SERVER_STARTED.register(AchievementManager::loadConfig);
    }
}