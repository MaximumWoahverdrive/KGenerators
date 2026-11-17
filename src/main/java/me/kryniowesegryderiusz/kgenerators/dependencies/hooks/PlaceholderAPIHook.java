package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.utils.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlaceholderAPIHook {

    public static String translatePlaceholders(OfflinePlayer op, String text) {

        if (op != null && Main.getDependencies().isEnabled(Dependency.PLACEHOLDERAPI)) {
            return PlaceholderAPI.setPlaceholders(op, text);
        } else return text;
    }

    public static ItemStack translatePlaceholders(OfflinePlayer op, ItemStack item) {

        if (op != null && Main.getDependencies().isEnabled(Dependency.PLACEHOLDERAPI) && item.hasItemMeta()) {
            // getItemMeta() returns a copy, so the changes only stick once it is set back
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) Text.setName(meta, PlaceholderAPI.setPlaceholders(op, Text.name(meta)));
            if (meta.hasLore()) Text.setLore(meta, PlaceholderAPI.setPlaceholders(op, Text.lore(meta)));
            item.setItemMeta(meta);
        }
        return item;
    }
}
