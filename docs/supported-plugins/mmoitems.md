# MMOItems

### What is included in MMOItems support?

* You can use MMOItems items in recipes file
* You can generate MMOItems items in items generator

### Using MMOItems items

Just set item material to: `mmoitems:<type>#<id>`&#x20;

ex. `mmoitems:MATERIAL#ENCHATED_IRON`

### Generating MMOItems items

To generate MMOItems item just set id as explained before in items generator, like below:

```yaml
generates:
    #type: item - generator will spawn simple item
    - type: item
      chance: 10.0
      item: "mmoitems:MATERIAL#ENCHATED_IRON"
```
