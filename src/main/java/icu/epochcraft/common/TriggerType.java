package icu.epochcraft.common;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 成就触发类型枚举，支持多达 100 种触发方式。
 */
public enum TriggerType {
    INVENTORY_CHANGED,
    CONSUME_ITEM,
    KILL_ENTITY,
    CUSTOM_COMMAND,
    BREAK_BLOCK,
    PLACE_BLOCK,
    USE_ITEM,
    FISHING_SUCCESS,
    ACHIEVE_LEVEL,
    ENTER_DIMENSION,
    LEAVE_DIMENSION,
    SLEEP_IN_BED,
    EAT_FOOD,
    BREW_POTION,
    CRAFT_ITEM,
    SMELT_ITEM,
    TRADE_WITH_VILLAGER,
    FILL_BUCKET,
    SHEAR_SHEEP,
    ENCHANT_ITEM,
    ANVIL_USE,
    BREED_ANIMALS,
    SPAWN_ENTITY,
    PICKUP_ITEM,
    DROP_ITEM,
    FLY_WITH_ELYTRA,
    CHORUS_FRUIT_TELEPORT,
    BE_IN_BIOME,
    SPRINT,
    SWIM,
    RIDE_MINECART,
    RIDE_BOAT,
    RIDE_PIG,
    RIDE_HORSE,
    BLOCK_EXPLODE,
    BECOME_INVISIBLE,
    GET_POISONED,
    DRINK_POTION,
    USE_CROSSBOW,
    SHOOT_BOW,
    USE_SHIELD,
    TAKE_FALL_DAMAGE,
    TAKE_FIRE_DAMAGE,
    TAKE_DROWNING_DAMAGE,
    TAKE_EXPLOSION_DAMAGE,
    SWIM_IN_LAVA,
    ENTER_NETHER,
    ENTER_END,
    DEFEAT_WITHER,
    DEFEAT_ENDER_DRAGON,
    CRAFT_DIAMOND,
    OBTAIN_NETHER_STAR,
    OPEN_ENDERCHEST,
    USE_BEACON,
    SUMMON_GOLEM,
    LIGHT_OBSIDIAN,
    MINE_GOLD,
    MINE_IRON,
    MINE_EMERALD,
    MINE_LAPIS,
    MINE_REDSTONE,
    MINE_COAL,
    GAIN_XP,
    LEVEL_UP,
    FIND_TREASURE,
    OPEN_SHULKER_BOX,
    PLACE_TORCH,
    LIGHT_TORCH,
    PLACE_REDSTONE,
    START_CARTOGRAPHY,
    MAP_FILL,
    USE_LOOM,
    USE_SMITHING_TABLE,
    USE_GRINDSTONE,
    USE_STONECUTTER,
    USE_BLAST_FURNACE,
    USE_SMOKER,
    USE_COMPOSTER,
    USE_BREWING_STAND,
    USE_CAULDRON,
    USE_HOE,
    TILL_SOIL,
    PLANT_CROP,
    HARVEST_CROP,
    OPEN_CHEST,
    OPEN_TRAPPED_CHEST,
    OPEN_BARREL,
    PLACE_NOTE_BLOCK,
    PLAY_NOTE_BLOCK,
    SPAWN_CREEPER,
    SPAWN_ENDERMAN,
    SPAWN_BLAZE,
    SPAWN_ZOMBIE,
    SPAWN_SKELETON,
    SPAWN_SLIME,
    SPAWN_PHANTOM,
    SPAWN_WITHER,
    SPAWN_DRAGON,
    CUSTOM_EVENT_01,
    CUSTOM_EVENT_02,
    CUSTOM_EVENT_03,
    CUSTOM_EVENT_04,
    CUSTOM_EVENT_05,
    CUSTOM_EVENT_06,
    CUSTOM_EVENT_07,
    CUSTOM_EVENT_08,
    CUSTOM_EVENT_09,
    CUSTOM_EVENT_10,
    CUSTOM_EVENT_11,
    CUSTOM_EVENT_12,
    CUSTOM_EVENT_13,
    CUSTOM_EVENT_14,
    CUSTOM_EVENT_15,
    CUSTOM_EVENT_16,
    CUSTOM_EVENT_17,
    CUSTOM_EVENT_18,
    CUSTOM_EVENT_19,
    CUSTOM_EVENT_20;

    private static final Map<String, TriggerType> LOOKUP = new HashMap<>();

    static {
        for (TriggerType trigger : values()) {
            LOOKUP.put(trigger.name().toLowerCase(Locale.ROOT), trigger);
        }
    }

    public static TriggerType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("触发类型不能为空");
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT).replace('-', '_');
        TriggerType trigger = LOOKUP.get(normalized);
        if (trigger == null) {
            throw new IllegalArgumentException("未知触发类型: " + value);
        }
        return trigger;
    }
}