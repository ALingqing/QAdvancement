# QAdvancement 自定义成就

Minecraft Fabric 客户端/服务端共用自定义成就模组

- 包名: `icu.epochcraft`
- Mod ID: `qadvancement`
- 作者: `阿清`
- 吉祥物: `player233lol`, `lonelyxiya`

## 说明

QAdvancement 是一款服务端驱动、客户端展示的自定义成就模组：

- 成就数据存放在 `config/qadvancement/achievements/` 中的 JSON 文件，由服务端读取和解析。
- 服主可直接增删改 JSON 文件，无需重新编译。
- 支持跨模组物品引用，如 `minecraft:diamond`、`thermal:copper_ingot` 等。
- 服务端负责成就判定、触发与奖励执行，客户端仅接收服务端发送的 GUI 数据并渲染显示。
- 客户端无需额外成就配置文件，只需安装 `qadvancement` 模组即可显示服务器发送的界面。
- 当前支持多达 100 种触发类型，包含基础事件和自定义类型。

## 特色功能

- 动态 JSON 成就配置
- 客户端 GUI：分类标签页、滚动列表、成就图标、完成状态
- 奖励支持：控制台命令、全服公告、经验值、物品
- 规则安全：服务端判定，防止作弊玩家伪造触发

## 使用方式

- 在创造模式物品栏中找到 `成就编辑器`（QAdvancement 自定义物品）。
- 右键使用该物品即可打开服务器端成就编辑器界面。
- 编辑后关闭界面，服务器会自动保存成就配置到 `config/qadvancement/achievements/`。
- 服务器端成就配置发生变化后，客户端安装该模组即可在游戏内显示。

## JSON 示例

```json
{
  "title": "钻石探索者",
  "description": "获得任意钻石物品后解锁。",
  "trigger": "inventory_changed",
  "trigger_value": "minecraft:diamond",
  "category": "资源",
  "icon": "minecraft:diamond",
  "rewards": [
    { "type": "experience", "value": "50" },
    { "type": "broadcast", "value": "&6玩家 %player% 获得了钻石探索者成就！" },
    { "type": "item", "value": "minecraft:diamond 2" }
  ]
}
```

## 成就项字段说明

- `title`：成就标题
- `description`：成就描述
- `trigger`：触发类型，参考 `TriggerType` 枚举
- `trigger_value`：触发条件值，例如物品 ID、实体 ID、指令码
- `category`：GUI 中分类标签
- `icon`：显示图标，对应物品 ID
- `rewards`：奖励列表

## 常用奖励类型

- `command`：执行命令，支持 `%player%` 替换
- `broadcast`：全服公告，支持 `&` 颜色代码
- `experience`：给予经验值
- `item`：给予物品，格式 `mod:item count`

## 触发类型说明

当前支持 100 个触发类型。常用类型包括：

- `inventory_changed`
- `consume_item`
- `kill_entity`
- `custom_command`
- `break_block`
- `place_block`
- `use_item`
- `craft_item`
- `smelt_item`
- `enter_dimension`

更多触发类型请查看 `src/main/java/icu/epochcraft/common/TriggerType.java`。

## 构建提示

- `gradle.properties` 中关键字段：
  - `mod_version`：模组版本
  - `minecraft_version`：Minecraft 版本
  - `fabric_loader_version`：Fabric Loader 版本
  - `fabric_api_version`：Fabric API 版本
  - `java_version`：Java 语言级别

使用 `./gradlew build` 或 `gradle build` 来编译。生成 Jar 位于 `build/libs/`。