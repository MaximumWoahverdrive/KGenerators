# Nexo

### What is included in Nexo support?

* You can use Nexo items in recipes file
* You can generate Nexo items in items generator
* You can generate Nexo blocks

### Using Nexo items

Just set item material to: `nexo:<nexo_material_id>` ex. `nexo:ruby`

### Generating Nexo items

To generate Nexo item just set id as explained before in items generator, like below:

```yaml
generates:
    #type: item - generator will spawn simple item
    - type: item
      chance: 10.0
      item: nexo:ruby
```

### Generating Nexo blocks

Generating Nexo blocks requires to add another type of generated object, like below:

```yaml
  generates:
    #type: nexo_block - generator will spawn Nexo block
    - type: nexo_block
      chance: 10.0
      material: ruby_ore
```

### Compatibility

Built against Nexo 1.10.0. Nexo itself supports Minecraft 1.20.4 and newer.
