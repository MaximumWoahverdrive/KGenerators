package me.kryniowesegryderiusz.kgenerators.utils;

import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import org.bukkit.entity.EntityType;

public class EntityUtils {

    public static EntityType getEntityType(String s, String place) {
        try {
            return EntityType.valueOf(s.toUpperCase(java.util.Locale.ROOT));
        } catch (Exception e) {
            Logger.error(place + ": " + s + " is not a proper entity! Using PIG!");
        }
        return EntityType.PIG;
    }
}
