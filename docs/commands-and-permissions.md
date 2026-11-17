# Commands & Permissions

### Commands with permissions

|                                                                          |                         |                                                     |
| ------------------------------------------------------------------------ | ----------------------- | --------------------------------------------------- |
| */kgenerators*                                                           | kgenerators.commands    | List all commands you have access to                |
| */kgenerators list*                                                      | kgenerators.list        | List all generators                                 |
| */kgenerators give \<player> \<generator\_ID> \[amount]*                 | kgenerators.give        | Give generator to player                            |
| */kgenerators getall*                                                    | kgenerators.getall      | Get all generators                                  |
| */kgenerators limits*                                                    | kgenerators.limits      | Check your limits                                   |
| */kgenerators actions*                                                   | kgenerators.actions     | Check possible actions                              |
| */kgenerators timeleft*                                                  | kgenerators.timeleft    | Check time remaining to regeneration                |
| */kgenerators menu*                                                      | kgenerators.menu        | Open generators menu                                |
| */kgenerators menu \<player>*                                            | kgenerators.menu.others | Open generators menu for player                     |
| */kgenerators menu \<player> \<chances/recipe/upgrade> \<generator\_ID>* | kgenerators.menu.others | Open specyfic generators menu for player            |
| */kgenerators upgrade*                                                   | kgenerators.upgrade     | Upgrade generator                                   |
| /kgenerators spawn \<world> \<x> \<y> \<z> \<generator\_ID> \[owner]     | kgenerators.spawn       | Spawn (appear) generator on specific coordinates    |
| /kgenerators spawn worldedit \<generator\_ID> \[owner]                   | kgenerators.spawn       | Spawn (appear) generator inside WorldEdit selection |
| /kgenerators remove worldedit                                            | kgenerators.remove      | Removes all generators in WorldEdit selection       |
| */kgenerators reload*                                                    | kgenerators.reload      | Reload config and messages                          |

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
| Drop to inventory    | kgenerators.droptoinventory         | Enables drop to inventory           |

### Limits permissions

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
