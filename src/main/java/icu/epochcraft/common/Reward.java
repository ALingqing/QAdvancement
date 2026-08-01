package icu.epochcraft.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 成就奖励定义
 */
public class Reward {
    public enum Type {
        COMMAND,
        BROADCAST,
        EXPERIENCE,
        ITEM
    }

    public final Type type;
    public final String value;

    public Reward(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public static List<Reward> parseRewards(JsonArray array) {
        List<Reward> rewards = new ArrayList<>();
        if (array == null) {
            return rewards;
        }
        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;
            JsonObject obj = element.getAsJsonObject();
            String type = obj.has("type") ? obj.get("type").getAsString().toLowerCase() : "";
            String value = obj.has("value") ? obj.get("value").getAsString() : "";
            switch (type) {
                case "command" -> rewards.add(new Reward(Type.COMMAND, value));
                case "broadcast" -> rewards.add(new Reward(Type.BROADCAST, value));
                case "experience" -> rewards.add(new Reward(Type.EXPERIENCE, value));
                case "item" -> rewards.add(new Reward(Type.ITEM, value));
                default -> {
                }
            }
        }
        return rewards;
    }
}