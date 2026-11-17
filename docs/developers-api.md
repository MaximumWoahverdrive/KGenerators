# Developers API

### Loading dependency

KGenerators Regenerated is published through [JitPack](https://jitpack.io/#MaximumWoahverdrive/KGenerators),
built from each release tag. Use it as a compile-only dependency; the server provides the plugin at
runtime. Add `depend: [KGenerators]` or `softdepend: [KGenerators]` to your `plugin.yml`.

#### Gradle

```groovy
repositories {
    maven { url = 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.MaximumWoahverdrive:KGenerators:VERSION'
}
```

#### Maven

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.MaximumWoahverdrive</groupId>
    <artifactId>KGenerators</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

> [!NOTE]
> *VERSION* is a release tag, such as `8.0.0`. The API is the same as KGenerators 7.3, so addons built
> against upstream's `me.kryniowesegryderiusz:kgenerators-core:7.3` from CodeMC keep working; see
> the [migration notes](migrating-to-8.0.md#for-developers) for the few internal classes that changed.

### Maven

```xml
<repositories>
  <repository>
    <id>codemc-repo</id>
    <url>https://repo.codemc.org/repository/maven-public/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>me.kryniowesegryderiusz</groupId>
    <artifactId>kgenerators-core</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

#### Gradle

```
repositories {
    maven { url "https://repo.codemc.org/repository/maven-public/" }
}

dependencies {
    compileOnly 'me.kryniowesegryderiusz:kgenerators-core:VERSION'
}
```

> [!WARNING]
> *VERSION* should look like `7.0` / `7.1 / 7.2` and so on

### Getting started with KGeneratorsAPI

**All API methods are accessible from** [**`KGeneratorsAPI`**](../src/main/java/me/kryniowesegryderiusz/kgenerators/api/KGeneratorsAPI.java) **class**

The most important Interface is [`IGeneratorLocation`](../src/main/java/me/kryniowesegryderiusz/kgenerators/api/interfaces/IGeneratorLocation.java), which manages Generator at specific location.

Related to that is method `KGeneratorsAPI#getLoadedGeneratorLocation(Location)`, which returns `null` if there is no loaded generator at that location

#### A few examples below

<details>

<summary>Example of getting generator from location</summary>

```java
IGeneratorLocation igl = KGeneratorsAPI.getLoadedGeneratorLocation(location);
```

</details>

<details>

<summary>Example of removing generator from location</summary>

`location` - Bukkit Location variable

```java
IGeneratorLocation igl = KGeneratorsAPI.getLoadedGeneratorLocation(location);
    if (igl != null)
        igl.removeGenerator(true, null);
```

</details>

<details>

<summary>Example of checking if mining generator is possible</summary>

This can be related for example to CustomEnchants/Minions plugins

`location` - Bukkit Location variable for mined block

`player` - Player related with this action

```java
IGeneratorLocation igl = KGeneratorsAPI.getLoadedGeneratorLocation(location);
    if (igl != null
        && igl.isPermittedToMine(player)
        && igl.isBlockPossibleToMine(location)) {
        //Your block removal code
        igl.scheduleGeneratorRegeneration();
    }
```

</details>

### Events

All events are in the `me.kryniowesegryderiusz.kgenerators.api.events` package.

* `PreGeneratorRegenerationEvent` - cancellable event called before a generator regenerates, returns [`IGeneratorLocation`](../src/main/java/me/kryniowesegryderiusz/kgenerators/api/interfaces/IGeneratorLocation.java)
* `PostGeneratorRegenerationEvent` - called after generator regeneration, for monitoring reasons, returns [`IGeneratorLocation`](../src/main/java/me/kryniowesegryderiusz/kgenerators/api/interfaces/IGeneratorLocation.java)
* `PostGeneratorPlaceEvent` - called after a generator is placed, returns the `IGeneratorLocation` and the `CommandSender` that placed it
* `GeneratorRemoveEvent` - called when a generator is removed from the world, returns the `IGeneratorLocation`, the generator item, whether it is dropped, and the player (if any)
* `PostGeneratorUpgradeGeneratorLocationEvent` - called after a placed generator is upgraded, returns the upgrade, the player and the `IGeneratorLocation`
* `PostGeneratorUpgradeItemEvent` - called after generator items are upgraded, returns the upgrade, the player and the item
* `GeneratorLoadEvent` / `GeneratorUnloadEvent` - called when a placed generator is loaded or unloaded with its chunk, returns the `IGeneratorLocation`
* `PluginEnabledEvent` / `PluginDisabledEvent` - called after KGenerators has fully loaded, and when it is disabled
* `PluginReloadEvent` - called, when KGenerators is being reloaded.
