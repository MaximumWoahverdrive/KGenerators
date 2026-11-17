# Text formatting

Every text in KGenerators' configuration and language files (messages, menu names and lore,
generator item names and lore, hologram lines) can be coloured in three ways, also mixed in one text.

### `&` colour codes

The classic Minecraft codes: `&0`-`&9` and `&a`-`&f` for colours, `&l` bold, `&o` italic,
`&n` underlined, `&m` strikethrough, `&k` obfuscated and `&r` reset.

```yaml
name: '&aCrops generator'
```

### Hex colours

`#RRGGBB` (or `&#RRGGBB`) sets any colour:

```yaml
name: '&aExample #4fff47generator'
```

### MiniMessage

[MiniMessage](https://docs.advntr.dev/minimessage/format.html) tags such as `<gradient>`,
`<rainbow>`, `<bold>` or `<#ff8800>`:

```yaml
name: '<gradient:#4fff47:#00aaff>Crops generator</gradient>'
lore:
- '<gray>Grows <green>fully grown</green> crops'
```

When a text mixes MiniMessage tags and `&` codes, an `&` colour code resets the formatting before it,
as it does in plain `&` text.

KGenerators' own placeholders, such as `<generator>` or `<amount>`, are not MiniMessage tags and
keep working inside MiniMessage text.

> [!NOTE]
> Click and hover tags (`<click>`, `<hover>`) have no effect: KGenerators turns every text into a
> coloured string before showing it.
