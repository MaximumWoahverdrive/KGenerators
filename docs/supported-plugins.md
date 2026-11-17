# Supported plugins

KGenerators hooks into these plugins when they are installed. None of them is required.

"Built against" is the version of each plugin's API that KGenerators Regenerated 8.0.0 is compiled
against. Other versions of a plugin work as long as the parts of its API that KGenerators uses haven't
changed; newer versions are not guaranteed to work. Some APIs are kept at an older version on purpose,
because newer ones no longer support Minecraft 1.19.4 or Java 17, which KGenerators still does.

| Plugin | What it adds | Built against |
| --- | --- | --- |
| [BentoBox](supported-plugins/bentobox.md) | Island flags, cleanup on island deletion | BentoBox 1.23.1 |
| [CMI](supported-plugins/cmi.md) | Generator holograms | CMI API 9.8.6.4 |
| [DecentHolograms](supported-plugins/decentholograms.md) | Generator holograms | DecentHolograms 2.10.2 |
| [EcoItems](supported-plugins/ecoitems.md) | Items in recipes and generators | EcoItems 4.0.0 (eco 6.53.0) |
| [FactionsUUID](supported-plugins/factionsuuid.md) | Claim protection | FactionsUUID 1.6.9.5-U0.6.40 |
| [FancyHolograms](supported-plugins/fancyholograms.md) | Generator holograms | FancyHolograms 2.8.0 |
| [IridiumSkyblock](supported-plugins/iridiumskyblock.md) | Cleanup on island deletion | IridiumSkyblock 4.1.5 |
| [ItemsAdder](supported-plugins/itemsadder.md) | Items and blocks in recipes and generators | ItemsAdder API 3.6.1 |
| [JetsMinions](supported-plugins/jetsminions.md) | Minions mine generators | JetsMinions 7.5.1 |
| [Lands](supported-plugins/lands.md) | Land protection | Lands API 7.0.2 |
| [LitMinions](supported-plugins/litminions.md) | Minions mine generators | LitMinions API 3.1.7 |
| [MMOItems](supported-plugins/mmoitems.md) | Items in recipes and generators | MMOItems 6.10.1 (MythicLib 1.7.1) |
| [MythicMobs](supported-plugins/mythicmobs.md) | Items and mobs in recipes and generators | MythicMobs 5.8.0 |
| [Nexo](supported-plugins/nexo.md) | Items and blocks in recipes and generators | Nexo 1.10.0 |
| [Oraxen](supported-plugins/oraxen.md) | Items and blocks in recipes and generators | Oraxen 1.175.0 |
| [PlaceholderAPI](supported-plugins/placeholderapi.md) | Placeholders | PlaceholderAPI 2.12.3 |
| [PlayerPoints](supported-plugins/playerpoints.md) | Upgrade cost in points | PlayerPoints 3.3.5 |
| [PlotSquared](supported-plugins/plotsquared.md) | Plot protection | PlotSquared 7.6.0 |
| [Slimefun](supported-plugins/slimefun.md) | Explosive tools don't break generators | Slimefun RC-37 |
| [SuperiorSkyblock2](supported-plugins/superiorskyblock2.md) | Island permissions, island level, cleanup on island deletion | SuperiorSkyblock2 API 2024.1 |
| [Vault](supported-plugins/vault.md) | Money for upgrades and custom drops | Vault API 1.7.1 |
| [WildStacker](supported-plugins/wildstacker.md) | Generator items aren't stacked in barrels | WildStacker API 2026.3 |
| [WorldEdit](supported-plugins/worldedit.md) | Spawn and remove generators in a selection | WorldEdit 7.2.20 |
| [WorldGuard](supported-plugins/worldguard.md) | Region flags | WorldGuard 7.0.8 |

Only one hologram plugin is used. If several are installed, KGenerators picks FancyHolograms first, then
DecentHolograms, then CMI.

> [!NOTE]
> The HolographicDisplays and Minions Revamped integrations were removed in 8.0.0. See
> [Upgrading from 7.3.x to 8.0.0](migrating-to-8.0.md#removed-integrations).
