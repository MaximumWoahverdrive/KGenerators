package me.kryniowesegryderiusz.kgenerators.api.interfaces;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public interface IUpgradeCost {
	
	/**
	 * Loads cost from config
	 * @param config the upgrades config to read from
	 * @param generatorId is config path for upgrade ex. "generatorId.cost"
	 * @return true if cost was found in config and loaded, false if this cost is not set up
	 * @throws CannnotLoadUpgradeException if the cost is set up but cannot be loaded
	 */
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException;
	
	/**
	 * Checks if requirements are met
	 * @param p the player upgrading the generators
	 * @param amount of generators being upgraded
	 * @return true if requirements are met
	 */
	public boolean checkRequirements(Player p, int amount);
	
	/**
	 * Takes requirements
	 * @param p the player to take the cost from
	 * @param amount of generators being upgraded
	 */
	public void takeRequirements(Player p, int amount);
	
	
	/**
	 * Gets lang formatted string for display reasons
	 * @param amount of generators being upgraded
	 * @return string with formatted cost
	 */
	public String getCostFormatted(int amount);

}
