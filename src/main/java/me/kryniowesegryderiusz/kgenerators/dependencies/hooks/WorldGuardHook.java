package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.WGFlag;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class WorldGuardHook {

	/** Null unless WorldGuard is installed and the flags were registered. */
	private static WorldGuardFlags flags;

	/** Registers KGenerators' flags. WorldGuard only accepts new flags while plugins load. */
	public static void registerFlags() {
		try {
			flags = new WorldGuardFlags();
			flags.worldGuardFlagsAdd();
		} catch (Exception | LinkageError e) {
			flags = null;
			Logger.error(e);
		}
	}

	public static boolean isHooked() {
		return flags != null && flags.isWorldGuardHooked();
	}
	
	public static boolean isPlayerAllowedToMine(Player player, Location location) {
		if (Main.getDependencies().isEnabled(Dependency.WORLD_GUARD) 
				&& !player.hasPermission("kgenerators.bypass.worldguard")
				&& flags.isWorldGuardFlagAllow(location, player, WGFlag.ONLY_GEN_BREAK)) {
			Lang.getMessageStorage().send(player, Message.GENERATORS_DIGGING_ONLY_GEN);
			return false;
		} else return true;
	}
	
	public static boolean isPlayerAllowedToPickUp(Player player, Location location) {
		if (Main.getDependencies().isEnabled(Dependency.WORLD_GUARD)) {
			return player.hasPermission("kgenerators.bypass.worldguard") 
					|| flags.isWorldGuardFlagAllow(location, player, WGFlag.PICK_UP);
		} else return true;
	}
	
	public static boolean isPlayerAllowedToInteract(Player player, Location location) {
		if (Main.getDependencies().isEnabled(Dependency.WORLD_GUARD)) {
			return player.hasPermission("kgenerators.bypass.worldguard") 
					|| flags.isWorldGuardFlagAllow(location, player, WGFlag.INTERACT);
		} else return true;
	}

}
