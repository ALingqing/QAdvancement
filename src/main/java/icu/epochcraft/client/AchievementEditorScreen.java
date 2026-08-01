package icu.epochcraft.client;

import icu.epochcraft.common.AchievementEditorScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AchievementEditorScreen extends HandledScreen<AchievementEditorScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/container/generic_54.png");

    public AchievementEditorScreen(AchievementEditorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 114 + handler.getRows() * 18;
    }

    @Override
    protected void init() {
        super.init();
        addDrawableChild(new ButtonWidget(this.x + this.backgroundWidth / 2 - 50, this.y + this.backgroundHeight - 22, 100, 20, Text.of("保存并关闭"), button -> onClose()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        int x = this.x;
        int y = this.y;
        this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, 3 + this.handler.getRows() * 18);
        drawTexture(matrices, x, y + 3 + this.handler.getRows() * 18, 0, 126, this.backgroundWidth, 96);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        drawCenteredText(matrices, this.textRenderer, this.title, this.backgroundWidth / 2, 6, 0xFFFFFF);
        drawTextWithShadow(matrices, this.textRenderer, this.playerInventoryTitle, 8, this.backgroundHeight - 96 + 2, 0xA0A0A0);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}