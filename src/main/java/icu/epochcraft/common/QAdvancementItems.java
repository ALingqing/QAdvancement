package icu.epochcraft.common;

import net.fabricmc.loader.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class QAdvancementItems {
    public static final Item ACHIEVEMENT_EDITOR = register("achievement_editor", new Item(createSettings()));

    public static void register() {
        // 确保类加载时执行注册
    }

    private static Item.Settings createSettings() {
        Item.Settings settings = new Item.Settings().maxCount(1);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            try {
                Class<?> builderClass = Class.forName("net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder");
                Method buildMethod = builderClass.getMethod("build", Identifier.class, Supplier.class);
                ItemGroup group = (ItemGroup) buildMethod.invoke(null, new Identifier("qadvancement", "general"), (Supplier<ItemStack>) () -> new ItemStack(Items.WRITABLE_BOOK));
                settings.group(group);
            } catch (Throwable ignored) {
            }
        }
        return settings;
    }

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier("qadvancement", id), item);
    }
}