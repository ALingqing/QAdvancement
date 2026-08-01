package icu.epochcraft.common;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.List;

/**
 * 自定义成就数据模型
 */
public class Achievement {
    public final Identifier id;
    public final String title;
    public final String description;
    public final TriggerType trigger;
    public final String triggerValue;
    public final String category;
    public final Identifier iconItem;
    public final List<Reward> rewards;

    public Achievement(Identifier id, String title, String description, TriggerType trigger, String triggerValue, String category, Identifier iconItem, List<Reward> rewards) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.trigger = trigger;
        this.triggerValue = triggerValue;
        this.category = category;
        this.iconItem = iconItem;
        this.rewards = rewards;
    }

    public static Achievement fromJson(Identifier id, JsonObject json) {
        String title = json.has("title") ? json.get("title").getAsString() : "自定义成就";
        String description = json.has("description") ? json.get("description").getAsString() : "未提供描述";
        TriggerType trigger = TriggerType.fromString(json.get("trigger").getAsString());
        String triggerValue = json.has("trigger_value") ? json.get("trigger_value").getAsString() : "";
        String category = json.has("category") ? json.get("category").getAsString() : "默认";
        Identifier iconItem = null;
        if (json.has("icon")) {
            try {
                iconItem = new Identifier(json.get("icon").getAsString());
            } catch (Exception ignored) {
            }
        }
        List<Reward> rewards = Reward.parseRewards(json.has("rewards") ? json.getAsJsonArray("rewards") : null);
        return new Achievement(id, title, description, trigger, triggerValue, category, iconItem, rewards);
    }
}