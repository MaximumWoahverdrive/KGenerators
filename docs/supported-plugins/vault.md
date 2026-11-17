# Vault

### What is included in Vault support?

* Money as a generator's upgrade cost (`cost` in `upgrades.yml`)
* Money in custom drops (`money` in `generators.yml`)

Vault needs an economy plugin installed to do this.

### How to configure upgrades.yml to include money?

```yaml
stone_generator_level_1:
  next-level: stone_generator_level_2
  cost: 500
```

### Compatibility

Built against Vault API 1.7.1.
