# EcoItems

### What is included in EcoItems support?

* You can use EcoItems items in recipes file
* You can generate EcoItems items in items generator

### Using EcoItems items

Just set item material to: `ecoitems:<ecoitems_material_id>` ex. `ecoitems:enchanted_emerald`

### Generating EcoItem items

To generate EcoItems item just set id as explained before in items generator, like below:

```yaml
generates:
    #type: item - generator will spawn simple item
    - type: item
      chance: 10.0
      item: ecoitems:enchanted_emerald
```

### Compatibility

Built against EcoItems 4.0.0 with eco 6.53.0 and libreforge 4.0.0, which cover Minecraft 1.17 - 1.19. Current eco and EcoItems versions (2026.x) only support Minecraft 1.21.8 and newer, and the hook may not work with them.
