# PlaceholderAPI

### Where can placeholders be used?

* All language files
* Generator item names and lore

### What placeholders are included?

* `%kgenerators_generators_placed_all%` Returns how many generators the player has placed
* `%kgenerators_generators_placed_<generatorID>%` Returns how many generators of that type the player has placed
* `%kgenerators_limits_limit_<limitID>%` Returns user limit (`-1` means no limit)
* `%kgenerators_limits_placed_<limitID>%` Returns user limit already used
* `%kgenerators_limits_left_<limitID>%` Returns how many more generators the user can place (`-1` means no limit)

The placeholders only work for online players.

See [Limits](../configuration-files/limits.md) for what the limit placeholders count.

### Compatibility

Built against PlaceholderAPI 2.12.3.
