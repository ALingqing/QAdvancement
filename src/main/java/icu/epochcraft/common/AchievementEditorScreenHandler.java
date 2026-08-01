package icu.epochcraft.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class AchievementEditorScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<AchievementEditorScreenHandler> TYPE = new ScreenHandlerType<>(AchievementEditorScreenHandler::new);
    private final SimpleInventory inventory;
    private final PlayerInventory playerInventory;

    public AchievementEditorScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(TYPE, syncId);
        this.playerInventory = playerInventory;
        this.inventory = new SimpleInventory(27);
        this.inventory.onOpen(playerInventory.player);
        addEditorSlots();
        addPlayerSlots();
        AchievementManager.fillEditorInventory(inventory);
    }

    private void addEditorSlots() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
    }

    private void addPlayerSlots() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
        if (!player.world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            AchievementManager.saveEditorInventory(serverPlayer, inventory);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (index < 27) {
                if (!insertItem(slotStack, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!insertItem(slotStack, 0, 27, false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return stack;
    }

    public int getRows() {
        return 3;
    }
}