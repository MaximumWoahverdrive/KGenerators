# BentoBox

### What is included in BentoBox support?

* KGenerators adds a few flags, which help players manage their island. You can translate them in your BentoBox lang files.
* All generators are being removed on island deletion

### Added KGenerators flags

**Flag: KGENERATORS\_USE\_FLAG**\
*Default: Member*\
Determines if players could mine generators on this island.

**Flag: KGENERATORS\_PICKUP\_FLAG**\
*Default: Member*\
Determines if players could pick up generators on this island.

**Flag: KGENERATORS\_OPEN\_MENU\_FLAG**\
*Default: Member*\
Determines if players could open generator's menus on this island.

### Flags configuration

The first time KGenerators starts with BentoBox installed, it adds a `bentobox-hook` section to `config.yml`. There you can set the default rank of each flag (`500` is Member):

```yaml
bentobox-hook:
  flags:
    KGENERATORS_PICKUP_FLAG:
      default-rank: 500
    KGENERATORS_USE_FLAG:
      default-rank: 500
    KGENERATORS_OPEN_MENU_FLAG:
      default-rank: 500
```

Players with `kgenerators.bypass.bentobox` ignore these flags.

BentoBox flags language are located in `plugins/BentoBox/locales/BentoBox/<lang>.yml` There you'll find also KGenerators lang settings

### Compatibility

Built against BentoBox 1.23.1. Newer BentoBox versions no longer support Minecraft 1.19.4, so KGenerators stays on that API version.
