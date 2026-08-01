package icu.epochcraft.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Locale;

/**
 * 成就管理器，负责加载配置、事件判定、奖励执行。
 */
public class AchievementManager {
    private static final Gson GSON = new Gson();
    private static final Map<Identifier, Achievement> ACHIEVEMENTS = new HashMap<>();
    private static final Map<TriggerType, List<Achievement>> TRIGGERS = new HashMap<>();
    private static final Map<UUID, Set<Identifier>> GRANTED = new HashMap<>();

    public static void init() {
        for (TriggerType type : TriggerType.values()) {
            TRIGGERS.put(type, new ArrayList<>());
        }
    }

    public static void loadConfig(MinecraftServer server) {
        ACHIEVEMENTS.clear();
        TRIGGERS.values().forEach(List::clear);
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("qadvancement").resolve("achievements");
        try {
            Files.createDirectories(configDir);
            if (!Files.exists(configDir)) {
                return;
            }
            Files.list(configDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path))) {
                            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                            Identifier id = new Identifier("qadvancement", path.getFileName().toString().replaceFirst("\\.json$", ""));
                            Achievement achievement = Achievement.fromJson(id, json);
                            ACHIEVEMENTS.put(id, achievement);
                            if (TRIGGERS.containsKey(achievement.trigger)) {
                                TRIGGERS.get(achievement.trigger).add(achievement);
                            }
                        } catch (Exception ex) {
                            server.sendSystemMessage(errorText("[QAdvancement] 加载成就失败: " + path.getFileName()));
                        }
                    });
        } catch (IOException e) {
            server.sendSystemMessage(errorText("[QAdvancement] 读取成就配置目录失败"));
        }
    }

    public static void reload(MinecraftServer server) {
        loadConfig(server);
        server.sendSystemMessage(successText("[QAdvancement] 成就配置已重新加载"));
    }

    public static void fillEditorInventory(Inventory inventory) {
        inventory.clear();
        for (int i = 0; i < 27 && i < inventory.size(); i++) {
            inventory.setStack(i, ItemStack.EMPTY);
        }
        int index = 0;
        for (Achievement achievement : ACHIEVEMENTS.values()) {
            if (index >= inventory.size()) break;
            inventory.setStack(index++, createAchievementBook(achievement));
        }
    }

    public static void saveEditorInventory(ServerPlayerEntity player, Inventory inventory) {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("qadvancement").resolve("achievements");
        try {
            Files.createDirectories(configDir);
            Files.list(configDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.isEmpty()) continue;
                JsonObject json = parseAchievementBook(stack);
                if (json == null) continue;
                Identifier id = getIdFromJson(json);
                if (id == null) continue;
                Path path = configDir.resolve(id.getPath() + ".json");
                Files.writeString(path, GSON.toJson(json));
            }
            loadConfig(player.getServer());
            player.sendMessage(successText("成就编辑器已保存并同步。"), false);
        } catch (IOException e) {
            player.sendMessage(errorText("保存成就配置失败，请检查服务器日志。"), false);
        }
    }

    private static ItemStack createAchievementBook(Achievement achievement) {
        ItemStack book = new ItemStack(Items.WRITABLE_BOOK);
        NbtCompound tag = new NbtCompound();
        tag.putString("title", achievement.title);
        tag.putString("author", "qadvancement");
        NbtList pages = new NbtList();
        JsonObject json = new JsonObject();
        json.addProperty("id", achievement.id.toString());
        json.addProperty("title", achievement.title);
        json.addProperty("description", achievement.description);
        json.addProperty("trigger", achievement.trigger.name().toLowerCase(Locale.ROOT));
        json.addProperty("trigger_value", achievement.triggerValue);
        json.addProperty("category", achievement.category);
        json.addProperty("icon", achievement.iconItem != null ? achievement.iconItem.toString() : "minecraft:book");
        json.add("rewards", GSON.toJsonTree(achievement.rewards));
        pages.add(NbtString.of(json.toString()));
        tag.put("pages", pages);
        book.setNbt(tag);
        return book;
    }

    private static JsonObject parseAchievementBook(ItemStack stack) {
        if (stack.getItem() != Items.WRITABLE_BOOK && stack.getItem() != Items.WRITTEN_BOOK) {
            return null;
        }
        if (!stack.hasNbt()) {
            return null;
        }
        NbtCompound tag = stack.getNbt();
        if (!tag.contains("pages")) {
            return null;
        }
        NbtList pages = tag.getList("pages", 8);
        if (pages.isEmpty()) {
            return null;
        }
        String page = pages.getString(0);
        try {
            return JsonParser.parseString(page).getAsJsonObject();
        } catch (Exception ex) {
            return null;
        }
    }

    private static Identifier getIdFromJson(JsonObject json) {
        if (json.has("id")) {
            try {
                return new Identifier(json.get("id").getAsString());
            } catch (Exception ignored) {
            }
        }
        if (json.has("title")) {
            String title = json.get("title").getAsString().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_\\-]", "_");
            if (title.isEmpty()) {
                return null;
            }
            return new Identifier("qadvancement", title);
        }
        return null;
    }

    public static void handleInventoryChanged(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) return;
        String itemId = Registry.ITEM.getId(stack.getItem()).toString();
        handleTrigger(player, TriggerType.INVENTORY_CHANGED, itemId);
    }

    public static void handleConsumeItem(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) return;
        String itemId = Registry.ITEM.getId(stack.getItem()).toString();
        handleTrigger(player, TriggerType.CONSUME_ITEM, itemId);
    }

    public static void handleKillEntity(ServerPlayerEntity player, String entityId) {
        handleTrigger(player, TriggerType.KILL_ENTITY, entityId);
    }

    public static void handleCustomCommand(ServerPlayerEntity player, String command) {
        handleTrigger(player, TriggerType.CUSTOM_COMMAND, command);
    }

    public static void grantAchievement(ServerPlayerEntity player, Achievement achievement) {
        UUID uuid = player.getUuid();
        GRANTED.putIfAbsent(uuid, new HashSet<>());
        if (GRANTED.get(uuid).contains(achievement.id)) {
            return;
        }
        GRANTED.get(uuid).add(achievement.id);
        ServerCommandSource source = player.getCommandSource();
        for (Reward reward : achievement.rewards) {
            switch (reward.type) {
                case COMMAND -> {
                    String command = reward.value.replace("%player%", player.getEntityName());
                    source.getServer().getCommandManager().execute(source.withSilent(), command);
                }
                case BROADCAST -> source.getServer().getPlayerManager().broadcast(successText(translateColorCodes(reward.value)), false);
                case EXPERIENCE -> {
                    try {
                        int amount = Integer.parseInt(reward.value);
                        player.giveExperiencePoints(amount);
                    } catch (NumberFormatException ignored) {
                    }
                }
                case ITEM -> {
                    String[] parts = reward.value.split(" ", 2);
                    Identifier itemId = new Identifier(parts[0]);
                    int count = 1;
                    if (parts.length > 1) {
                        try {
                            count = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    Item item = Registry.ITEM.getOrEmpty(itemId).orElse(Items.AIR);
                    if (item != Items.AIR) {
                        player.giveItemStack(new ItemStack(item, count));
                    }
                }
                default -> {
                }
            }
        }
        player.sendMessage(successText("已获得成就: " + achievement.title), false);
    }

    private static void handleTrigger(ServerPlayerEntity player, TriggerType trigger, String value) {
        List<Achievement> achievementList = TRIGGERS.get(trigger);
        if (achievementList == null) return;
        for (Achievement achievement : achievementList) {
            if (achievement.triggerValue.equalsIgnoreCase(value)) {
                grantAchievement(player, achievement);
            }
        }
    }

    private static Text errorText(String message) {
        return new LiteralText(message).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF5555)));
    }

    private static Text successText(String message) {
        return new LiteralText(message).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x55FF55)));
    }

    private static String translateColorCodes(String text) {
        return text.replace('&', '§');
    }

    public static Map<Identifier, Achievement> getAchievements() {
        return ACHIEVEMENTS;
    }

    public static Achievement getAchievement(Identifier id) {
        return ACHIEVEMENTS.get(id);
    }

    public static boolean hasGranted(ServerPlayerEntity player, Identifier achievementId) {
        UUID uuid = player.getUuid();
        return GRANTED.containsKey(uuid) && GRANTED.get(uuid).contains(achievementId);
    }
}