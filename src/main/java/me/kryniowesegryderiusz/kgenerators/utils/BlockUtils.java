package me.kryniowesegryderiusz.kgenerators.utils;

import me.kryniowesegryderiusz.kgenerators.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;

public final class BlockUtils {

    private BlockUtils() {}

    public static ItemStack getItemStackByBlock(Block block) {
        return ItemUtils.getItemStackFromMaterial(block.getType());
    }

    public static void setBlock(Location location, ItemStack item) {
        location.getBlock().setType(item.getType());
    }

    public static void setBlock(Location location, Material material, boolean physics) {
        location.getBlock().setType(material, physics);
    }

    public static void setBlock(Location location, Material material) {
        location.getBlock().setType(material);
    }

    public static boolean isOnWhitelist(Block block) {
        return Main.getSettings().isOnWhitelist(block.getType());
    }

    public static boolean isAir(Block block) {
        return block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR;
    }

    public static boolean isWater(Block block) {
        return block.getType() == Material.WATER;
    }

    public static boolean isAgeable(Block block) {
        return block.getBlockData() instanceof Ageable;
    }

    public static int getMaximumAge(Block block) {
        return ((Ageable) block.getBlockData()).getMaximumAge();
    }

    public static int getAge(Block block) {
        return ((Ageable) block.getBlockData()).getAge();
    }

    public static void setAge(Block block, int age) {
        Ageable ageable = (Ageable) block.getBlockData();
        ageable.setAge(age);
        block.setBlockData(ageable);
    }
}
