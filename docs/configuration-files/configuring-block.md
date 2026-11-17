# Configuring Block

### Where items configuration are supported?

* Generated block
* Placeholder

### What can I set?

* [Material](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)
* Custom head via `material: "customhead:<base64>"`

### Example configurations

#### Standard block
This is just stone

```yaml
material: STONE
```


#### Custom head
This is custom head block.

You can get base64 **texture value** from [Minecraft heads](https://minecraft-heads.com/player-heads) or from [player/skin](https://mineskin.org/)

```yaml
material: customhead:ewogICJ0aW1lc3RhbXAiIDogMTY1NDEzNTEyMjkwOSwKICAicHJvZmlsZUlkIiA6ICI5ZGY3MmQyMTIzZDc0MWRiOWZmNGFlYzljZWIxNTRlNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJLcnluaW8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQxMGMwZjI4ZDBjOTFkMGZlODFjZmUzZjZlMjMwYmFhN2YxOTY5NTlhOGI4NDBkN2U2ZDdlMDNjNjYzNDBkNSIKICAgIH0KICB9Cn0=
```
