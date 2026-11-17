# MythicMobs

### What is included in MythicMobs support?

* You can use MythicMobs items in recipes file
* You can generate MythicMobs items in items generator
* You can generate MythicMobs mobs

### Using MythicMobs items

Just set item material to: `mythicmobs:<mythicmobs_id>` ex. `mythicmobs:KingsCrown`

### Generating MythicMobs items

To generate MythicMobs item just set id as explained before in items generator, like below:

```yaml
generates:
    #type: item - generator will spawn simple item
    - type: item
      chance: 10.0
      item: mythicmobs:KingsCrown
```

### Generating MythicMobs entities

To generate MythicMobs entity just set id similar as explained before in items generator.

Just set entity type to: `mythicmobs:<mythicmobs_id>` ex. `mythicmobs:SkeletonKing`

Example below:

```yaml
  generates:
    #type: entity - generator will spawn entity
    - type: entity
      chance: 10.0
      entity: "mythicmobs:SkeletonKing"
    #type: entity - generator will spawn more complicated entity.
    - type: entity
      chance: 10.0
      entity:
        type: "mythicmobs:SkeletonKing" #only this is required
        amount: 2
      #How many living entities would stop regeneration
      max-entities: 1
```
