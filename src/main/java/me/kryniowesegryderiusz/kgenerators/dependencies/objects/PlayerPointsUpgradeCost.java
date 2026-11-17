package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.PlayerPointsHook;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.PaymentUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class PlayerPointsUpgradeCost implements IUpgradeCost, PaymentUpgradeCost {
	
	public PlayerPointsUpgradeCost() {}
	
	private int cost;

	@Override
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException {
		if (!config.contains(generatorId+".player-points"))
			return false;
				
		cost = config.getInt(generatorId+".player-points");
		return true;
	}

	@Override
	public boolean checkRequirements(Player p, int amount) {
        return this.cost * amount <= PlayerPointsHook.getPlayerPoints(p);
    }

	@Override
	public void takeRequirements(Player p, int amount) {
		if (!pay(p, amount))
			Logger.warn("Upgrades: PlayerPoints refused to take " + this.cost * amount + " points from " + p.getName());
	}

	@Override
	public boolean pay(Player p, int amount) {
		return PlayerPointsHook.takePlayerPoints(p, this.cost * amount);
	}

	@Override
	public void refund(Player p, int amount) {
		PlayerPointsHook.givePlayerPoints(p, this.cost * amount);
	}

	@Override
	public String getCostFormatted(int amount) {
		return amount*this.cost + " " + Lang.getMessageStorage().get(Message.UPGRADES_COST_PLAYERPOINTS);
	}

}
