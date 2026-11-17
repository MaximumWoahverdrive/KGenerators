package me.kryniowesegryderiusz.kgenerators.api.interfaces;

import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IGeneratorLocation {
	
	/**
	 * Gets the location of the generated block.
	 * @return location, where generated object would appear
	 */
	Location getGeneratedBlockLocation();
	
	/**
	 * Checks if block at location is physically possible to mine.
	 * Should be checked with every mining action
	 * @param location - Crucial for double generators. 
	 * @return true if the location is the generated block's location and the block there is not the placeholder
	 */
	boolean isBlockPossibleToMine(Location location);
	
	/**
	 * Checks if player is permitted to mine generated block
	 * Should be checked with every player-related mining action
	 * You probably wanna use {@link #isBlockPossibleToMine(Location)} at first
	 * @param player the player trying to mine
	 * @return true if player is permitted
	 */
	boolean isPermittedToMine(Player player);
	
	/**
	 * Checks whether this generatorLocation is corrupted.
	 * @return true if generator is not scheduled and there is nothing to mine
	 */
	boolean isBroken();
	
	/**
	 * Instantly regenerates generator
	 */
	void regenerateGenerator();
	
	/**
	 * Schedules generator regeneration based on generator settings
	 */
	void scheduleGeneratorRegeneration();
	
	/**
	 * Removes generator from world and database
	 * @param drop - whether generator item should be spawned
	 * @param player - player removing generator
	 */
	void removeGenerator(boolean drop, @Nullable Player player);
	
	/**
	 * Pick up generator
	 * @param player - player, who is picking up generator
	 */
	void pickUpGenerator(Player player);
}
