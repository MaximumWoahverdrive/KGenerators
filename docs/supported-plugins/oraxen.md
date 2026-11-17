# Oraxen

### What is included in Oraxen support?

* You can use Oraxen items in recipes file
* You can generate Oraxen items in items generator
* You can generate Oraxen blocks

### Using Oraxen items

Just set item material to: `oraxen:<oraxen_material_id>` ex. `oraxen:amethyst_ore`

### Generating Oraxen items

To generate Oraxen item just set id as explained before in items generator, like below:

```yaml
generates:
    #type: item - generator will spawn simple item
    - type: item
      chance: 10.0
      item: oraxen:amethyst_ore
```

### Generating Oraxen blocks

Generating Oraxen blocks requires to add another type of generated object, like below:

```yaml
  generates:
    #type: oraxen_block- generator will spawn Oraxen block
    - type: oraxen_block
      chance: 10.0
      material: oraxen:amethyst_ore
```

### Compatibility

Built against Oraxen 1.175.0, the newest version that runs on Java 17 (Oraxen 1.177 and later need Java 21).
