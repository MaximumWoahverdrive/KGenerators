# ItemsAdder

### What is included in ItemsAdder support?

* You can use ItemsAdder items in recipes file
* You can generate ItemsAdder items in items generator
* You can generate ItemsAdder blocks

### Using ItemsAdder items

Just set item material to: `itemsadder:<itemsadder_material_id>` ex. `itemsadder:ruby`

### Generating ItemsAdder items

To generate IA item just set id as explained before in items generator, like below:

```yaml
generates:
    #type: item - generator will spawn simple item
    - type: item
      chance: 10.0
      item: itemsadder:ruby
```

### Generating ItemsAdder blocks

Generating IA blocks requires to add another type of generated object, like below:

```yaml
  generates:
    #type: itemsadder_block - generator will spawn ItemsAdder block
    - type: itemsadder_block
      chance: 10.0
      material: ruby_ore
```

### Compatibility

Built against ItemsAdder API 3.6.1.
