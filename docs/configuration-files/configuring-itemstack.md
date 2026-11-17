# Configuring ItemStack

### Where items configuration are supported?

There are a few places, where you can extensively edit Minecraft items. They include:

* Generator item
* Generated item
* Recipes file
* Custom drops item

### What can I set?

Using extended item configuration its possible to set:

* [Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)
* Amount
* Name
* Lore
* [Item Flags](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/ItemFlag.html)
* [Enchants](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/Enchantment.html)
* Custom NBT
* Custom head via `material: "customhead:<base64>"`
* Custom Model Data

### Simple vs extended configuration

At any place, where item is about to be configured there are two options possible to achieve that. Simple configuration saves space and is perfect, where you only want to set item material. Extended configuration on the other hand gives you the opportunity to freely customise ItemStack.

#### Simple configuration
Simple configuration saves space and is perfect, where you only want to set item material.&#x20;

```yaml
...:
  item: STONE
```


#### Extended configuration
Extended configuration on the other hand gives you the opportunity to freely customise ItemStack.

```yaml
...:
 item:
   material: STONE
   amount: 1
   name: "Magic stone"
   lore:
    - "&cThats Magic Stone"
   enchants:
   - FORTUNE:1
   item-flags:
   - HIDE_ATTRIBUTES
   custom-nbt:
   - "custom_nbt_key:custom nbt value"
   custom-model-data: 155
```

> [!NOTE]
> In KGenerators Regenerated, `custom-nbt` entries are stored as string values in the item's
> persistent data container, not as raw NBT tags: `key:value` is stored as `kgenerators:key`,
> and `namespace:key:value` as `namespace:key`. Keys may only use `a-z`, `0-9`, `.`, `_`, `-`
> and `/`.


#### Custom head
This is custom head item.

You can get base64 **texture value** from [Minecraft heads](https://minecraft-heads.com/player-heads) or from [player/skin](https://mineskin.org/)

It can be set alone

```yaml
...:
  item: customhead:ewogICJ0aW1lc3RhbXAiIDogMTY1NDEzNTEyMjkwOSwKICAicHJvZmlsZUlkIiA6ICI5ZGY3MmQyMTIzZDc0MWRiOWZmNGFlYzljZWIxNTRlNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJLcnluaW8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQxMGMwZjI4ZDBjOTFkMGZlODFjZmUzZjZlMjMwYmFhN2YxOTY5NTlhOGI4NDBkN2U2ZDdlMDNjNjYzNDBkNSIKICAgIH0KICB9Cn0=
```

Or in extended item configuration

```yaml
...:
    item:
    material: customhead:ewogICJ0aW1lc3RhbXAiIDogMTY1NDEzNTEyMjkwOSwKICAicHJvZmlsZUlkIiA6ICI5ZGY3MmQyMTIzZDc0MWRiOWZmNGFlYzljZWIxNTRlNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJLcnluaW8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQxMGMwZjI4ZDBjOTFkMGZlODFjZmUzZjZlMjMwYmFhN2YxOTY5NTlhOGI4NDBkN2U2ZDdlMDNjNjYzNDBkNSIKICAgIH0KICB9Cn0=
    name: "Head"
    lore:
     - "&cThats just player head"
```


#### Extended configuration examples

#### Extended generator item

```yaml
example_generator:
  #... other configuration
  generator-item:
    material: GRASS
    name: '&aExample generator'
    #and so on...
```


#### Extended generated item

```yaml
example_generator:
  #...
  generates:
    #...
    - type: item
      chance: 10.0
      item:
        material: STONE #only this is required
        amount: 5
        name: "Magic stone"
        #and so on...
```


#### Custom drops item

```yaml
example_generator:
  #...
  generates:
    #...
    - type: block
      chance: 10.0
      material: STONE
      custom-drops:
        #...
        item:
          material: DIAMOND
          name: "&bDiamond"
          #and so on...
```


#### Custom recipe item

```yaml
example_generator:
  #...
  ingredients:
    #...
    A:
      material: STONE
      name: "Magic stone"
      #and so on...
```
