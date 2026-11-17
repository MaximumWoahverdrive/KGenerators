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

## Text

Colour codes work as before (`&` codes and `#RRGGBB` hex colours). Two differences:

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
- `@Nullable` annotations come from `org.jetbrains.annotations` instead of `javax.annotation`.
