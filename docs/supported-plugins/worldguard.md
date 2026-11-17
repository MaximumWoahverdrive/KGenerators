# WorldGuard

### What is included in WorldGuard support?

* Flags that help managing KGenerators on regions

Players with `kgenerators.bypass.worldguard` ignore these flags.

### Flags

**Flag: kgenerators-pick-up**\
*Default: allow*\
When set to *deny* players will not be able to pick up generator, even if *breaking* is enabled.

**Flag: kgenerators-interact**\
*Default: allow*\
When set to *deny* players will not be able to perform any configured action on generator.

**Flag: kgenerators-only-gen-break**\
*Default: deny*\
When set to *allow* players will only be able to dig generators in the area. They wouldn't break any other blocks in region.\
**Warning! It doesnt override other flags!** If players do not have permission to break blocks in area, they still wont be able to break generators!

### How to create public generators?

If you want to create public generators make sure, that you:

* made region with higher priority than others **or** set child region
* set *kgenerators-pick-up* to *deny*
* set *kgenerators-only-gen-break* to *allow*
* set *kgenerators-interact* to *deny*
* also remember to set *block-break* to *allow*

### Compatibility

Built against WorldGuard 7.0.8 (with WorldEdit 7.2.20). WorldGuard 7.0.9 and later dropped Minecraft 1.19.4.
