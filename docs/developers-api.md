# Developers API

### Loading dependency

> [!NOTE]
> The artifacts below are upstream's, published on CodeMC up to version 7.3. KGenerators Regenerated
> keeps the same API, so you can compile against them; it does not publish its own artifacts yet.

You can add KGenerators as dependency directly via jar file or use Mave/Gradle

#### Maven

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

**All API methods are accessible from** [**`KGeneratorsAPI`**](../core/src/main/java/me/kryniowesegryderiusz/kgenerators/api/KGeneratorsAPI.java) **class**

The most important Interface is [`IGeneratorLocation`](../core/src/main/java/me/kryniowesegryderiusz/kgenerators/api/interfaces/IGeneratorLocation.java), which manages Generator at specific location.

Related to that is method `KGeneratorsAPI#getGeneratorLocation(Location)`

#### A few examples below

<details>

<summary>Example of getting generator from location</summary>

```java
IGeneratorLocation igl = KGeneratorsAPI.getGeneratorLocation(location);
```

</details>

<details>

<summary>Example of removing generator from location</summary>

`location` - Bukkit Location variable

```java
IGeneratorLocation igl = KGeneratorsAPI.getGeneratorLocation(location);
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
IGeneratorLocation igl = KGeneratorsAPI.getGeneratorLocation(location);
    if (igl != null
        && igl.isPermittedToMine(player)
        && igl.isBlockPossibleToMine(location)) {
        //Your block removal code
        igl.scheduleGeneratorRegeneration();
    }
```

</details>

### Events

* `PreBlockGenerationEvent` - cancellable event called when generator regenegenerating function is fired, returns [`IGeneratorLocation`](../core/src/main/java/me/kryniowesegryderiusz/kgenerators/api/interfaces/IGeneratorLocation.java)\`\`
* `PostBlockGenerationEvent` - called after generator regeneration, for monitoring reasons, returns [`IGeneratorLocation`](../core/src/main/java/me/kryniowesegryderiusz/kgenerators/api/interfaces/IGeneratorLocation.java)\`\`
* `ReloadEvent` - called, when KGenerators is being reloaded.
