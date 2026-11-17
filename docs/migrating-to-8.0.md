# Upgrading from 7.3.x to 8.0.0

> [!NOTE]
> 8.0.0 is still in development; this list is updated as changes land.

8.0.0 modernises KGenerators and drops support for old and unmaintained platforms. If your server
is not covered by the requirements below, stay on
[7.3.1](https://github.com/MaximumWoahverdrive/KGenerators/releases/tag/7.3.1), the last release
that runs on Spigot and on Minecraft 1.8.8 - 1.19.3.

Configuration files, commands, permissions, the database and placed generators carry over
unchanged unless listed below.

## Server requirements

- **Paper 1.19.4 or newer, or a Paper fork** (Purpur, Folia, ...). Plain Spigot/CraftBukkit is
  no longer supported; KGenerators disables itself there and says so in the console.
- **Java 17 or newer.**

## Removed integrations

- **HolographicDisplays**: archived by its author in March 2024. Use DecentHolograms,
  FancyHolograms or CMI for generator holograms.
- **Minions Revamped**: unmaintained since September 2024.

## Generator items

Generator items now store their generator id in Paper's item data instead of an NBT tag.
Generator items made by 7.3.1 and older keep working: they are still recognised by their old tag.

- **Keep `generator-item-matcher.check-by-nbt: true`** in `config.yml` (the default). It is what
  recognises existing generator items, old and new.
- **`check-by-item-meta` no longer recognises items made by 7.3.1 or older.** It compares the whole
  item, and new generator items carry data the old ones lack. If you had turned `check-by-nbt`
  off and relied on name/lore matching, turn `check-by-nbt` back on before upgrading, or players'
  existing generator items will stop being recognised.

## `custom-nbt` in item configurations

`custom-nbt` entries are now stored as string values in the item's persistent data container, not
as raw NBT tags: `key:value` becomes `kgenerators:key`, and `namespace:key:value` becomes
`namespace:key`. Keys may only use `a-z`, `0-9`, `.`, `_`, `-` and `/`. If another plugin read
these values as raw NBT tags, it will no longer find them; plugins that use the persistent data
container can read them under the new keys.

## Behaviour fixes

- **`physics:` on generated blocks now works.** It was ignored before, so blocks were always placed
  without physics. It now defaults to off, so generators that don't set it behave as before;
  `physics: true` now actually applies physics.
- **Upgrades paid with Vault money or PlayerPoints** no longer go through when the economy plugin
  refuses the payment; anything already taken is refunded.
- **`/kgen list`** no longer puts the message prefix on every line.
- **`/kgen menu <player> upgrade <generator>`** opens the upgrade menu; it always answered that the menu
  doesn't exist.
- **`/kgen spawn`** without an owner, or for an offline owner, no longer fails with an error when
  limits are enabled; such generators are not subject to place limits. Spawned generators now count
  towards their owner's limits. Spawning at coordinates checks its arguments and the world, and works
  from the console on Folia.
- **Limits:**
  - `kgenerators.placelimit.<limit_id>.<number>` permissions now work for limit ids with capital
    letters, and only match their own limit: with limits `stone` and `stone_generators`,
    `kgenerators.placelimit.stone_generators.40` no longer also raises `stone` to 40. Permissions set
    to `false` no longer count, and on a group without `place-limit` such a permission no longer turns the player's unlimited group into a limited one.
  - Upgrading a placed generator is refused if it would take the owner over a place limit.
  - The "can't place more" message shows the player's own limit instead of the group's default.
  - `%kgenerators_limits_left_<limit_id>%` is `-1` for unlimited groups and never negative.
- **`/kgen timeleft`** names the right permission (`kgenerators.timeleft`) when it's missing.
- **Commands that only players can use** now tell the console so (new message
  `commands.any.player-only`, added to existing language files automatically) instead of printing to
  the server log.
- **Menus:**
  - Clicking in your own inventory while a KGenerators menu is open no longer presses the menu button in
    the same slot (for example picking up the generator).
  - Dragging items over a menu no longer loses them.
  - Menus no longer stay registered after they are closed, so later clicks in other inventories aren't
    treated as menu clicks.
  - `/kgen menu <player> ...` tells the sender it opened the menu (`commands.menu.opened-others`).
- **Generators no longer pop off as an item** when a regeneration finds its generated block still in
  place (after a cancelled break or piston push), and a piston push that ends up cancelled no longer
  schedules a regeneration.
- **Upgrades:** an upgrade whose `next-level` generator doesn't exist is reported when loading instead
  of taking the player's payment and failing. Experience costs check the player's current experience,
  not all experience they ever collected.
- **Generators that weren't saved on shutdown:** a chunk that loaded or unloaded just before `/stop`
  could stop all generators and schedules from being saved.
- **Ownerless generators** (`/kgen spawn` without an owner) are saved without an owner instead of with
  the "none" text, which made them owned by a player of that name after a restart.
- **A database error while loading a chunk** is retried instead of leaving that chunk's generators
  unloaded.
- **`/kgen reload`** now removes `disabled-worlds` entries and generator options that were deleted from
  the config, and no longer changes how fast generators regenerate when `generation-check` changed.
- **`/kgen give`** refuses amounts below 1, and its message no longer replaces the command name in the
  player's name.
- **`/kgen convertdbto`** refuses unknown database types (a typo used to convert to SQLite) and a second
  conversion while one is running, and no longer leaves chunk loading switched off if reading the old
  database fails.
- **Custom drops:** `exp-drop-to-eq` decides whether custom-drop experience goes to the inventory
  (it followed `block-drop-to-eq`). Minions no longer fail on custom drops without an item, and
  JetsMinions no longer drops the whole item when only part of it didn't fit into the chest.
- **Integrations:** worlds with WorldGuard regions turned off no longer cause errors; island deletions
  (BentoBox, SuperiorSkyblock2, IridiumSkyblock) also remove generators below y=0, and IridiumSkyblock
  deletions cancelled by another plugin no longer remove generators; an unknown MythicMobs mob no longer
  causes an error on every regeneration.
- **Folia:** `/kgen give`, `/kgen remove`, menus and custom-drop commands run on the right thread.
- **Holograms** of generators whose name contains `$` no longer stop all holograms from updating.
- **`lang: zhcn`** now uses the bundled Chinese translation instead of an English copy.
- **Malformed config values** (an item with no material, a non-list `generators:` in limits, a
  non-text sound name, a generated object with `chance: 0`, a generator without `delay`, a FancyHolograms
  background colour above 255, ...) are reported as errors instead of stopping the file from loading.

## Text

Colour codes work as before (`&` codes and `#RRGGBB` hex colours), and texts can now also use
[MiniMessage](configuration-files/text-formatting.md) tags such as `<gradient>`. Two other
differences:

- PlaceholderAPI placeholders in generator item names and lore are now actually applied. In 7.3.1
  they were silently left as they were.
- Text containing `§` codes is treated the same as `&` codes.

## For developers

- The developer API (`KGeneratorsAPI`, events, interfaces) is unchanged.
- `GeneratorItemMatcher.GENERATOR_ID_NBT` is deprecated; generator items carry
  `ItemData.GENERATOR_ID` (`kgenerators:generator-id`) in their persistent data container.
- `MultiVersionManager` and `Main.getMultiVersion()` are gone, along with the `multiversion`
  package: every supported server uses the same code now.
  - Block helpers are static methods on `utils.BlockUtils`, skull helpers on `utils.SkullUtils`.
  - WorldGuard flags are handled by `dependencies.hooks.WorldGuardHook`.
  - Colours and action bars: use Paper's Adventure API (`Player#sendActionBar`, components).
- The `debug.multi-version` setting in `config.yml` no longer does anything and can be removed.
- The unused `GeneratingType` and `UpgradeType` enums are removed.
- A few unused internal methods were removed, among them `IDatabase.getDataSource()`,
  `MenusManager.isVieving`/`isSomeoneVieving`, `RecipesManager.isGeneratorRecipe` and the
  `ItemsAdderHook` block helpers, and the unused `Settings.getGuis` and `isAsyncChunkLoading` (the
  `async-chunk-loading` option never did anything). `MenusManager.getVieving` returns `Enum<?>`, and
  `MenuItemStorage.get` takes an `IMenuItemType`.
- The API is published on JitPack: `com.github.MaximumWoahverdrive:KGenerators:<version>` (see
  [Developers API](developers-api.md)).
- `Dependency.VAULT_PERMISSIONS` and `VaultHook.setupPermissions`/`hasPermission` are removed; they
  were never used.
- `@Nullable` annotations come from `org.jetbrains.annotations` instead of `javax.annotation`.
