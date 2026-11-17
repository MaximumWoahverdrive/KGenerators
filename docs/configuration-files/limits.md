# Limits

### What are limits?

Limits are configured in `limits.yml`. A limit is a group of generators with rules that apply to every
player:

* **Place limit** - how many generators of the group one player may have placed at the same time
* **Only owner pick up** - only the player who placed a generator may pick it up
* **Only owner use** - only the player who placed a generator may mine its generated blocks

A player's place limit can be raised per player with permissions, and players can check their limits
in the limits menu.

### Enabling limits

Limits are off by default. Set `enabled: true` at the top of `limits.yml` and run `/kgenerators reload`
(or restart the server):

```yaml
enabled: true
```

While limits are disabled, none of the groups below are loaded, and the console says
`Limits are disabled. You can enable them in limits.yml!`.

### Configuring a limit group

Every top-level section other than `enabled` is a limit group. Its key is the **limit id**, which is used
in permissions and placeholders. The section called `example_limit_group` is only an explanation and is
never loaded.

> [!WARNING]
> Use **lowercase** limit ids (`stone_limits`, not `StoneLimits`). Permissions are read in lowercase, so
> the [permissions](#permissions) below never match a limit id that contains capital letters.

This is the example from the default `limits.yml`:

```yaml
#Unique limit id
example_limit_group:
  #Name of that group (display purposes)
  name: "Example limit"
  #Display item of that group (display purposes)
  item: STONE
  #List of generators affected in this limit group. Could be one.
  #So in this example below people'll be able to place only 10 stone generators, no matter, which level they have
  generators:
  - example_generator_id_level_1
  - example_generator_id_level_2
  #Default number of placed generators of that type per player
  #Set to -1 to disable that limit
  #!!! Can be overwritten by permission: kgenerators.placelimit.<limit_id>.<limit> (limit_id is the first line of limit section ex. global_limits)
  place-limit: 10
  #Should only owner be able to pick up generator?
  only-owner-pickup: true
  #Should only owner be able to break blocks of that generator?
  #Disables also piston-push event
  #Doesnt work for third-party plugins ex. breaking blocks hoppers
  only-owner-use: false
```

| Option | Required | Default | Description |
| --- | --- | --- | --- |
| `name` | yes | - | Name of the group, shown in the limits menu and in the "can't place more" message. |
| `item` | yes | - | Item shown for the group in the limits menu. See [Display item](#display-item). |
| `generators` | yes, except for `global_limits` | - | List of generator ids from `generators.yml` that belong to this group. See [Which generators a group covers](#which-generators-a-group-covers). |
| `place-limit` | no | `-1` | How many generators of this group one player may have placed. `-1` means no limit. |
| `only-owner-pickup` | no | `false` | If `true`, only the owner of a generator in this group can pick it up. |
| `only-owner-use` | no | `false` | If `true`, only the owner of a generator in this group can mine its generated blocks, and pistons can't push them. |

If `name` or `item` is missing, or a listed generator doesn't exist, the console reports an error and that
whole limit group is not loaded. The other groups still load.

#### Which generators a group covers

`generators` lists generator ids. A group may contain one generator, or several (for example all levels
of the same generator), in which case they share one place limit.

A group without `generators` only loads if its id is `global_limits`: that group covers every generator.
Any other group without `generators` is reported as an error and skipped.

#### Display item

`item` takes a single value (not an [extended item configuration](configuring-itemstack.md)):

* a material, for example `STONE`
* `generator:<generator_id>` - the generator item of that generator
* `customhead:<base64>` - a custom head (see [Configuring ItemStack](configuring-itemstack.md))
* an item from a [supported plugin](../supported-plugins.md), for example `itemsadder:ruby`

`PLAYER_HEAD` shows the head of the player who opens the menu.

### Preconfigured limits

The default `limits.yml` ships with these groups. You can edit or remove them and add your own.

```yaml
#This limits players to have max 50 generators at all
#It affects all generators (because they are not listed)
global_limits:
  name: "Global limit"
  item: PLAYER_HEAD
  place-limit: 50

#This limits players to have max 20 stone generators at all, no matter, which level they have
#Additionally only owner can pick up them
stone_generators_limits:
  name: "Stone generators limit"
  item: STONE
  generators:
  - stone_generator_level_1
  - stone_generator_level_2
  place-limit: 20
  only-owner-pickup: true

#This limits players to have max 10 stone_generator_level_1 and sets it's item to generator's item
stone_generator_level_1_limits:
  name: "Stone generator level 1 limit"
  item: generator:stone_generator_level_1
  generators:
  - stone_generator_level_1
  place-limit: 10

#This limits ores generators to be used only by owners
ore_generators_owner_limits:
  name: "Ores generators owner limit"
  item: generator:ores_generator
  generators:
  - ores_generator
  - ores_from_stone_generator
  only-owner-pickup: true
  only-owner-use: true
```

### How limits are counted and enforced

#### Place limits

* A player's count for a group is the number of placed generators they **own** of every generator in the
  group, added together. The owner is the player who placed the generator, or the owner given to
  `/kgenerators spawn`. Generators without an owner don't count for anyone.
* Generators count in every world, whether their chunk is loaded or not. The count is read from the
  database the first time it's needed and then kept up to date as generators are placed, picked up,
  removed and upgraded.
* When a player places a generator, every group that contains it is checked. If the player already has
  as many generators of a group placed as their limit allows, placing is refused with the
  `generators.limits.cant-place-more-type` message, which names the group.
* Several groups can contain the same generator; the generator can only be placed if every one of them
  allows it. In the preconfigured file, placing `stone_generator_level_1` is checked against
  `global_limits` (50), `stone_generators_limits` (20) and `stone_generator_level_1_limits` (10).
* `/kgenerators spawn` checks the owner's limits only when the owner is online; generators spawned
  without an owner or for an offline owner aren't limited, but still count for their owner.
* Upgrading a placed generator moves it from the old generator's count to the new one's. The upgrade is
  refused if that would take the owner over the limit of a group that contains the new generator but
  not the old one. Upgrading generator items in the hand isn't limited; the limit applies when they are
  placed.
* Lowering a limit doesn't remove generators that are already placed.

#### Only owner pick up and only owner use

* `only-owner-pickup`: when a player who isn't the owner tries to pick up a generator of the group,
  it's refused with the `generators.limits.cant-pick-up` message.
* `only-owner-use`: when a player who isn't the owner mines a generated block of a generator in the
  group, the break is cancelled and the `generators.limits.cant-use` message is shown. Pistons can't
  push generated blocks of such generators either. Other plugins that break blocks themselves (for
  example hoppers or minions) are not stopped.
* Generators without an owner are not affected.
* Players with `kgenerators.bypass.onlyownerchecks` are not affected.

These checks come on top of the usual permissions (`kgenerators.pickup.<generator_id>`,
`kgenerators.mine.<generator_id>`) and the protection of [supported plugins](../supported-plugins.md).

### Permissions

| Permission | Description |
| --- | --- |
| `kgenerators.placelimit.<limit_id>.<number>` | Sets the player's place limit for that group to `<number>`, if it's higher than the group's `place-limit`. If a player has several of these for one group, the highest number counts. |
| `kgenerators.placelimit.<limit_id>.bypass` | No place limit for that group. |
| `kgenerators.bypass.onlyownerchecks` | Ignore `only-owner-pickup` and `only-owner-use` of every group. |
| `kgenerators.limits` | Use `/kgenerators limits`. |

For example, `kgenerators.placelimit.stone_generators_limits.40` lets a player place 40 stone
generators instead of 20.

Notes on the `placelimit` permissions:

* They have to be given to the player (or their group) directly. Operator status and wildcard
  permissions such as `kgenerators.*` or `kgenerators.placelimit.*` don't grant them.
* A `kgenerators.placelimit.<limit_id>.<number>` permission can only raise a limit; on a group without
  `place-limit` it has no effect. A permission set to `false` doesn't count.
* The limit id in the permission is not case-sensitive.

### Limits menu

Players can see their limits in the limits menu. It opens with `/kgenerators limits` (permission
`kgenerators.limits`), or with the **Limits** button in the main menu (`/kgenerators menu`). The button
is only shown when limits are enabled and at least one group was loaded.

The menu shows one item per limit group with the group's name and display item, how many generators of
the group the player has placed out of their limit (`∞` when there is none), which generators the group
covers (or "all generators"), and whether only owners can use or pick them up.

The menu's title, size, items, slots and texts are configured in the `limits:` section of
`plugins/KGenerators/lang/<lang>_gui.yml`. The `limit` item has 18 slots by default; if you have more
groups than slots, the console reports an error and the extra groups aren't shown, so add slots there.
The placeholders available in the `limit` item are:

| Placeholder | Value |
| --- | --- |
| `<limit_display_item>` | The group's `item` (use it as the `item:` of the menu item) |
| `<limit_name>` | The group's `name` |
| `<amount>` | Generators of the group the player has placed |
| `<limit>` | The player's place limit, or `∞` |
| `<generators_list>` | Generators of the group (`additional-lines.limits.generator-list`), or `additional-lines.limits.all-generators` |
| `<is_only_owner_use>` | `additional-lines.limits.only-owner-use` if the group has `only-owner-use: true` |
| `<is_only_owner_pick_up>` | `additional-lines.limits.only-owner-pick-up` if the group has `only-owner-pickup: true` |

### Placeholders

With [PlaceholderAPI](../supported-plugins/placeholderapi.md) installed, these placeholders show a
player's limits:

* `%kgenerators_limits_limit_<limit_id>%` - the player's place limit (`-1` means no limit)
* `%kgenerators_limits_placed_<limit_id>%` - generators of the group the player has placed
* `%kgenerators_limits_left_<limit_id>%` - how many more the player can place (`-1` means no limit)
