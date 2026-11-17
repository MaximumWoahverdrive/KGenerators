# Commands & Permissions

`/kgenerators` can also be typed as `/kgen` or `/gen`.

None of these permissions is given to players by default: operators have them, everyone else needs them
granted by a permissions plugin. The `kgenerators.placelimit` permissions are the exception; see
[Limits](configuration-files/limits.md#permissions).

### Commands with permissions

Players need `kgenerators.commands` for every command, in addition to the command's own permission.
The console can run every command except those marked as player only.

|                                                                          |                         |                                                     |
| ------------------------------------------------------------------------ | ----------------------- | --------------------------------------------------- |
| */kgenerators*                                                           | kgenerators.commands    | List all commands you have access to                |
| */kgenerators list*                                                      | kgenerators.list        | List all generators                                 |
| */kgenerators give \<player> \<generator\_ID> \[amount]*                 | kgenerators.give        | Give generator to player                            |
| */kgenerators getall*                                                    | kgenerators.getall      | Get all generators (player only)                    |
| */kgenerators limits*                                                    | kgenerators.limits      | Check your [limits](configuration-files/limits.md) (player only) |
| */kgenerators actions*                                                   | kgenerators.actions     | Check possible actions                              |
| */kgenerators timeleft*                                                  | kgenerators.timeleft    | Check time remaining to regeneration of the generator you are looking at (player only) |
| */kgenerators menu*                                                      | kgenerators.menu        | Open generators menu (player only)                  |
| */kgenerators menu \<player>*                                            | kgenerators.menu.others | Open generators menu for player                     |
| */kgenerators menu \<player> \<chances/recipe/upgrade> \<generator\_ID>* | kgenerators.menu.others | Open specific generators menu for player            |
| */kgenerators upgrade*                                                   | kgenerators.upgrade     | Upgrade the generators in your hand (player only)   |
| */kgenerators spawn \<world> \<x> \<y> \<z> \<generator\_ID> \[owner]*   | kgenerators.spawn       | Spawn (appear) generator on specific coordinates    |
| */kgenerators spawn worldedit \<generator\_ID> \[owner]*                 | kgenerators.spawn       | Spawn (appear) generator inside [WorldEdit](supported-plugins/worldedit.md) selection (player only) |
| */kgenerators remove worldedit*                                          | kgenerators.remove      | Removes all generators in [WorldEdit](supported-plugins/worldedit.md) selection (player only) |
| */kgenerators remove owner \<player>*                                    | kgenerators.remove      | Removes all generators owned by player              |
| */kgenerators reload*                                                    | kgenerators.reload      | Reload config and messages                          |
| */kgenerators debug*                                                     | kgenerators.debug       | Upload debug information to mclo.gs and show the link |
| *kgenerators convertdbto \<MYSQL/SQLITE>*                                | console only            | [Convert the database](converting-databases.md)     |

**Parameters:**

* <> - needed
* \[] - optional

### Generators usage permissions

|                      |                                     |                                     |
| -------------------- | ----------------------------------- | ----------------------------------- |
| Placing generator    | kgenerators.place.\<generator\_id>  | Permission for placing generator    |
| Picking up generator | kgenerators.pickup.\<generator\_id> | Permission for picking up generator |
| Crafting generator   | kgenerators.craft.\<generator\_id>  | Permission for crafting generator   |
| Mining generator     | kgenerators.mine.\<generator\_id>   | Permission for mining generator     |
| Upgrading generator  | kgenerators.upgrade                 | Permission for upgrading generators |
| Drop to inventory    | kgenerators.droptoinventory         | Enables drop to inventory           |

### Limits permissions

See [Limits](configuration-files/limits.md#permissions) for details.

|                                               |                                              |                                                  |
| --------------------------------------------- | -------------------------------------------- | ------------------------------------------------ |
| Custom limit                                  | kgenerators.placelimit.\<limit\_id>.\<limit> | Permission for increasing generator place limit  |
| Bypass limit (for admins)                     | kgenerators.placelimit.\<limit\_id>.bypass   | Permission for overriding generator place limit. |
| Bypass limit's only-owner checks (for admins) | kgenerators.bypass.onlyownerchecks           | Permission for overriding owner checks           |

### Supported plugins permissions

|                                                   |                                      |                                                              |
| ------------------------------------------------- | ------------------------------------ | ------------------------------------------------------------ |
| WorldGuard flags bypass (for admins)              | kgenerators.bypass.worldguard        | Permission for overriding all WorldGuard restrictions        |
| BentoBox flags bypass (for admins)                | kgenerators.bypass.bentobox          | Permission for overriding all BentoBox restrictions          |
| SuperiorSkyblock2 permissions bypass (for admins) | kgenerators.bypass.superiorskyblock2 | Permission for overriding all SuperiorSkyblock2 restrictions |
| PlotSquared protection bypass (for admins)        | kgenerators.bypass.plotsquared       | Permission for bypassing KGenerators P2 protection           |
| FactionsUUID protection bypass (for admins)       | kgenerators.bypass.factionsuuid      | Permission for overriding all FactionsUUID restrictions      |
| Lands protection bypass (for admins)              | kgenerators.bypass.lands             | Permission for overriding all Lands restrictions             |
