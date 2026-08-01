package icu.epochcraft.common;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.player.PlayerPickupItemCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.LivingEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import icu.epochcraft.common.QAdvancementItems;
import icu.epochcraft.common.AchievementEditorScreenHandler;

/**
 * 事件注册器，处理服务端成就触发监听。
 */
public class AchievementEvents {
    public static void register() {
        PlayerPickupItemCallback.EVENT.register((player, itemEntity) -> {
            if (!player.world.isClient && player instanceof ServerPlayerEntity) {
                ItemStack stack = itemEntity.getStack();
                AchievementManager.handleInventoryChanged((ServerPlayerEntity) player, stack);
            }
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                ItemStack stack = serverPlayer.getStackInHand(hand);
                if (stack.getItem() == QAdvancementItems.ACHIEVEMENT_EDITOR) {
                    serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId, inv, p) -> new AchievementEditorScreenHandler(syncId, inv),
                            new LiteralText("成就编辑器")));
                    return TypedActionResult.consume(stack);
                }
                if (!stack.isEmpty() && stack.getItem().isFood()) {
                    AchievementManager.handleConsumeItem(serverPlayer, stack);
                }
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((ServerWorld world, LivingEntity killer, LivingEntity killed) -> {
            if (!world.isClient && killer instanceof ServerPlayerEntity) {
                String entityId = Registry.ENTITY_TYPE.getId(killed.getType()).toString();
                AchievementManager.handleKillEntity((ServerPlayerEntity) killer, entityId);
            }
        });
    }
}