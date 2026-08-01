package icu.epochcraft.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.TextureManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QAdvancementScreen extends Screen {
    private final AchievementEntry[] entries;
    private AchievementListWidget listWidget;
    private final List<String> categories = new ArrayList<>();
    private int selectedCategoryIndex = 0;
    private ButtonWidget closeButton;

    protected QAdvancementScreen(String[] ids, String[] titles, String[] descriptions, String[] categories, String[] icons, boolean[] completed, Text title) {
        super(title);
        this.entries = new AchievementEntry[ids.length];
        for (int i = 0; i < ids.length; i++) {
            this.entries[i] = new AchievementEntry(ids[i], titles[i], descriptions[i], categories[i], icons[i], completed[i]);
            if (!this.categories.contains(categories[i])) {
                this.categories.add(categories[i]);
            }
        }
    }

    @Override
    protected void init() {
        int listLeft = 20;
        int listRight = width / 3;
        int listTop = 40;
        int listBottom = height - 50;
        this.listWidget = new AchievementListWidget(client, listRight - listLeft, listBottom - listTop, listTop, listBottom, 24);
        addSelectableChild(this.listWidget);
        rebuildList();
        this.listWidget.setSelected(this.listWidget.getEntry(0));
        closeButton = addDrawableChild(new ButtonWidget(width / 2 - 100, height - 35, 200, 20, new LiteralText("关闭"), button -> close()));
    }

    private void rebuildList() {
        this.listWidget.clearEntries();
        String category = categories.isEmpty() ? "默认" : categories.get(selectedCategoryIndex);
        for (AchievementEntry entry : entries) {
            if (entry.category.equals(category)) {
                this.listWidget.addEntry(entry);
            }
        }
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        fill(matrices, 0, 0, width, height, 0xFF1A1A1A);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, title, width / 2, 12, 0xFFFFFF);
        renderTabs(matrices);
        listWidget.render(matrices, mouseX, mouseY, delta);
        drawSelectedDetails(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void renderTabs(MatrixStack matrices) {
        int x = 20;
        int y = 30;
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            int width = textRenderer.getWidth(category) + 14;
            int color = i == selectedCategoryIndex ? 0xFF5555FF : 0xFF555555;
            fill(matrices, x, y, x + width, y + 20, color);
            drawCenteredText(matrices, textRenderer, new LiteralText(category), x + width / 2, y + 6, 0xFFFFFF);
            x += width + 4;
        }
    }

    private void drawSelectedDetails(MatrixStack matrices) {
        AchievementEntry entry = listWidget.getSelectedOrNull();
        if (entry == null) {
            return;
        }
        int x = width / 3 + 30;
        int y = 40;
        fill(matrices, x - 10, y - 10, width - 20, height - 60, 0xFF262626);
        drawTextWithShadow(matrices, textRenderer, new LiteralText(entry.title), x, y, 0xFFFFAA);
        drawTextWithShadow(matrices, textRenderer, new LiteralText("类型: " + entry.category), x, y + 16, 0xAAAAAA);
        drawTextWithShadow(matrices, textRenderer, new LiteralText(entry.completed ? "状态: 已完成" : "状态: 未完成"), x, y + 28, entry.completed ? 0x55FF55 : 0xFF5555);
        drawTextWithShadow(matrices, textRenderer, new LiteralText(entry.description), x, y + 48, 0xCCCCCC);
        drawTextWithShadow(matrices, textRenderer, new LiteralText("触发: " + entry.id), x, height - 70, 0x999999);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY >= 30 && mouseY <= 50) {
            int x = 20;
            for (int i = 0; i < categories.size(); i++) {
                String category = categories.get(i);
                int width = textRenderer.getWidth(category) + 14;
                if (mouseX >= x && mouseX <= x + width) {
                    selectedCategoryIndex = i;
                    rebuildList();
                    return true;
                }
                x += width + 4;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    public static class AchievementEntry extends AlwaysSelectedEntryListWidget.Entry<AchievementEntry> {
        private final String id;
        private final String title;
        private final String description;
        private final String category;
        private final String icon;
        private final boolean completed;

        public AchievementEntry(String id, String title, String description, String category, String icon, boolean completed) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.category = category;
            this.icon = icon;
            this.completed = completed;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            fill(matrices, x, y, x + entryWidth, y + entryHeight, completed ? 0xFF223322 : 0xFF222222);
            drawTextWithShadow(matrices, textRenderer, new LiteralText(title), x + 5, y + 3, completed ? 0x88FF88 : 0xFFFFFF);
            drawTextWithShadow(matrices, textRenderer, new LiteralText(category), x + 5, y + 14, 0xAAAAAA);
            drawTextWithShadow(matrices, textRenderer, new LiteralText(completed ? "已完成" : "未完成"), x + entryWidth - 60, y + 3, completed ? 0x55FF55 : 0xFF5555);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }
    }

    public static class AchievementListWidget extends AlwaysSelectedEntryListWidget<AchievementEntry> {
        public AchievementListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            setRenderBackground(false);
            setRenderTopAndBottom(false);
        }

        @Override
        protected boolean isFocused() {
            return true;
        }
    }
}