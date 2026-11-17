# SuperiorSkyblock2

### What is included in SuperiorSkyblock2 support?

* KGenerators makes sure that island level is properly adjusted after block generation
* All generators are beeing removed on island deletion
* KGenerators adds a few flags, which help players manage their island. You can translate them in your SuperiorSkyblock2 lang files.
* SuperiorSkyblock2 block stacker cant stack generators

### Permissions

**Permission: kgenerators\_use\_flag**\
*Default: Member*\
Determines if players could mine generators on this island.

**Permission: kgenerators\_pickup\_flag**\
*Default: Member*\
Determines if players could pick up generators on this island.

**Permission: kgenerators\_open\_menu\_flag**\
*Default: Member*\
Determines if players could open generator's menu on this island.

### Permissions language files

> [!WARNING]
> You'll have to **manually** add these permissions to *SuperiorSkyblock2/menus/permission.yml*

Make sure that you **type permission ids in lowercase**

It should look similar to that:

#### kgenerators\_use\_flag

```yaml
  kgenerators_use_flag:
    display-menu: true
    permission-enabled:
      type: STONE
      name: '&6Generators usage'
      lore:
        - '&7Access to using generators inside the island.'
        - '&7Currently &aENABLED&7.'
    permission-disabled:
      name: '&6Generators usage'
      type: STONE
      lore:
        - '&7Access to using generators inside the island.'
        - '&7Currently &cDISABLED&7.'
    role-permission:
      name: '&6Generators usage'
      type: STONE
      lore:
        - '&7Access to using generators inside the island.'
        - '&7Role: &e{}&7.'
        - ''
        - '{0}'
    has-access:
      sound:
        type: ENTITY_EXPERIENCE_ORB_PICKUP
        volume: 0.2
        pitch: 0.2
    no-access:
      sound:
        type: BLOCK_ANVIL_PLACE
        volume: 0.2
        pitch: 0.2
```


#### kgenerators\_pickup\_flag

```yaml
  kgenerators_pickup_flag:
    display-menu: true
    permission-enabled:
      type: STONE
      name: '&6Generators pick up'
      lore:
        - '&7Access to pick up generators inside the island.'
        - '&7Currently &aENABLED&7.'
    permission-disabled:
      name: '&6Generators pick up'
      type: STONE
      lore:
        - '&7Access to pick up generators inside the island.'
        - '&7Currently &cDISABLED&7.'
    role-permission:
      name: '&6Generators pick up'
      type: STONE
      lore:
        - '&7Access to pick up generators inside the island.'
        - '&7Role: &e{}&7.'
        - ''
        - '{0}'
    has-access:
      sound:
        type: ENTITY_EXPERIENCE_ORB_PICKUP
        volume: 0.2
        pitch: 0.2
    no-access:
      sound:
        type: BLOCK_ANVIL_PLACE
        volume: 0.2
        pitch: 0.2
```


#### kgenerators\_open\_menu\_flag

```yaml
  kgenerators_open_menu_flag:
    display-menu: true
    permission-enabled:
      type: STONE
      name: '&6Generators open menu'
      lore:
        - "&7Access to opening generator's menu inside the island."
        - '&7Currently &aENABLED&7.'
    permission-disabled:
      name: '&6Generators open menu'
      type: STONE
      lore:
        - "&7Access to opening generator's menu inside the island."
        - '&7Currently &cDISABLED&7.'
    role-permission:
      name: '&6Generators open menu'
      type: STONE
      lore:
        - "&7Access to opening generator's menu inside the island."
        - '&7Role: &e{}&7.'
        - ''
        - '{0}'
    has-access:
      sound:
        type: ENTITY_EXPERIENCE_ORB_PICKUP
        volume: 0.2
        pitch: 0.2
    no-access:
      sound:
        type: BLOCK_ANVIL_PLACE
        volume: 0.2
        pitch: 0.2
```
