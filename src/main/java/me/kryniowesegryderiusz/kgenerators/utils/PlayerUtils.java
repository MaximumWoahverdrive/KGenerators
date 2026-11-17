package me.kryniowesegryderiusz.kgenerators.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import me.kryniowesegryderiusz.kgenerators.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
    /*
     * Standard features
     */

    public static void dropToInventory(Player p, Location alternativeLocation, ItemStack... items) {
        dropToInventory(p, alternativeLocation, new ArrayList<>(Arrays.asList(items)));
    }

    public static void dropToInventory(Player p, Location alternativeLocation, ArrayList<ItemStack> items) {
        if (p != null) {
            for (ItemStack item : items) {
                HashMap<Integer, ItemStack> left = p.getInventory().addItem(item.clone());
                if (!left.isEmpty()) {
                    for (ItemStack i : left.values()) {
                        p.getLocation().getWorld().dropItem(p.getLocation(), i);
                    }
                }
            }
        } else {
            for (ItemStack item : items) {
                alternativeLocation.getWorld().dropItem(alternativeLocation, item);
            }
        }
    }

    /**
     * Performs block drop to eq and permission checks for drop
     * @param p player to give the items to, nullable
     * @param location location to drop the items at if they are not given to the player
     * @param items items to drop
     */
    public static void dropBlockToInventory(Player p, Location location, ItemStack... items) {
        if (p != null && Main.getSettings().isBlockDropToEq() && p.hasPermission("kgenerators.droptoinventory"))
            dropToInventory(p, location, items);
        else dropToInventory(null, location, items);
    }
}
