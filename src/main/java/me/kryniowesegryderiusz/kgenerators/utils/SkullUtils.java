package me.kryniowesegryderiusz.kgenerators.utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import me.kryniowesegryderiusz.kgenerators.Main;

public final class SkullUtils {

	private SkullUtils() {}
	
	public static ItemStack itemFromName(String name) {
		ItemStack is = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		meta.setOwner(name);
		is.setItemMeta(meta);
		return is;
	}

	public static ItemStack itemFromBase64(String base64) {

		ItemStack is = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		
		meta.setPlayerProfile(getProfile(base64));

		is.setItemMeta(meta);
		
		return is;
	}

	public static void blockWithBase64(Block block, String skullBase64) {

		if (block.getType() != Material.PLAYER_HEAD) {
			block.setType(Material.PLAYER_HEAD);
		}

        Skull skull = (Skull) block.getState();
        skull.setPlayerProfile(getProfile(skullBase64));
        skull.update(true);
		
	}
	
	private static PlayerProfile getProfile(String base64) {
		PlayerProfile profile = Main.getInstance().getServer().createProfile(
				UUID.nameUUIDFromBytes("custom-textures".getBytes(StandardCharsets.UTF_8)), "custom-textures");
		profile.getProperties().add(new ProfileProperty("textures", base64));
		return profile;
	}

}
