# Optional settings

### What does it mean that some option is set somehow by default?

KGenerators config includes a lot of **optional** features, that you dont have to set to not mess your config with unused ones.

You can delete *Optional* options and they will automatically be set to their default values, so you dont have to bother.

In any time you can add them basing on the example generator in the [default generators.yml](../../src/main/resources/generators.yml), which lists every option with its default value

### Example

Here you have two configurations exampled that **works the same**:

```yaml
stone_generator_level_1:
  generator-item:
    material: END_STONE
    name: '&aStone generator I'
    lore:
    - '&6This is stone generator!'
    - '&6It generates stone!'
  delay: 40
  type: double
  generates:
    - type: block
      chance: 10.0
      material: STONE
  # *** Optional generators settings below ***
  generate-immediately-after-place: true #Set same as default
  placeholder: AIR #Set same as default
  allow-piston-push: true #Set same as default
  hologram: false #Set same as default
```

```yaml
stone_generator_level_1:
  generator-item:
    material: END_STONE
    name: '&aStone generator I'
    lore:
    - '&6This is stone generator!'
    - '&6It generates stone!'
  delay: 40
  type: double
  generates:
    - type: block
      chance: 10.0
      material: STONE
```

See? Works same and your generators.yml seems neater :D
