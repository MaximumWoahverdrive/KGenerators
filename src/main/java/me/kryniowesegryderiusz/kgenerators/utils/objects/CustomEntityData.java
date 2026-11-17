package me.kryniowesegryderiusz.kgenerators.utils.objects;

import java.util.ArrayList;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.MythicMobsHook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.EntityUtils;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.Text;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class CustomEntityData {

    @Getter
    private EntityType entityType;

    @Getter
    private String entityTypeString;

    @Getter
    @Setter
    private int amount = 1;

    @Getter
    @Setter
    private String name;

    /** MythicMobs mob id for {@code mythicmobs:<id>} entities, otherwise null */
    private String mythicMob;

    public CustomEntityData(String entityTypeString) {
        this.entityTypeString = entityTypeString;
        if (!entityTypeString.contains(":"))
            this.entityType = EntityUtils.getEntityType(entityTypeString, "CustomEntityData");
        else {
            String[] splitted = entityTypeString.split(":");
            if (splitted[0].equalsIgnoreCase("mythicmobs")) {
                this.mythicMob = splitted[1];
                this.entityType = MythicMobsHook.getEntityType(splitted[1]);
            } else {
                Logger.error("CustomEntityData: " + entityTypeString + " is not a proper entity! Using PIG!");
                this.entityType = EntityType.PIG;
            }
        }
    }

    public ArrayList<Entity> spawnEntities(Location location) {

        ArrayList<Entity> spawnedEntities = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            if (this.mythicMob != null) {
                Entity e = MythicMobsHook.spawnMythicMob(this.mythicMob, location);
                if (e != null) spawnedEntities.add(e);
            } else {
                Entity e = location.getWorld().spawnEntity(location, entityType);
                if (name != null) e.customName(Text.component(Text.colorize(name)));
                spawnedEntities.add(e);
            }
        }

        return spawnedEntities;
    }

    public ItemStack getItem() {
        return ItemUtils.parseItemStack(
                this.entityType.name() + "_SPAWN_EGG", "Generators file: GeneratedEntity", false);
    }

    public String toString() {
        return "Entity: " + this.entityTypeString + "[" + amount + " " + name + "]";
    }

    /**
     * Checks entityType
     * @param customEntityData entity data to compare with
     * @return true if xMaterials are same
     */
    public boolean isSimilar(CustomEntityData customEntityData) {
        return entityTypeString.equals(customEntityData.getEntityTypeString());
    }

    public static CustomEntityData load(Map<?, ?> configMap, String keyName) {

        if (configMap.containsKey(keyName)) {
            if (configMap.get(keyName) instanceof String) {
                return new CustomEntityData((String) configMap.get(keyName));
            } else {
                Map<?, ?> map;

                if (configMap.get(keyName) instanceof MemorySection section) map = section.getValues(false);
                else map = (Map<?, ?>) configMap.get(keyName);

                if (map != null && map.containsKey("type")) {
                    // CustomEntityData e = new CustomEntityData(EntityUtils.getEntityType((String) map.get("type"),
                    // "CustomEntityData"));
                    CustomEntityData e = new CustomEntityData((String) map.get("type"));
                    if (map.containsKey("amount")) e.setAmount((int) map.get("amount"));
                    if (map.containsKey("name")) e.setName((String) map.get("name"));
                    return e;
                }
            }
        }
        return null;
    }
}
