package me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.VaultHook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class UpgradeCostMoney implements IUpgradeCost, PaymentUpgradeCost {
	
	public UpgradeCostMoney() {}
	
	double cost;

	@Override
	public boolean checkRequirements(Player p, int amount) {
		return this.cost*amount <= VaultHook.getBalance(p);
	}

	@Override
	public void takeRequirements(Player p, int amount) {
		if (!pay(p, amount))
			Logger.warn("Upgrades: The economy plugin refused to take " + this.cost * amount + " from " + p.getName());
	}

	@Override
	public boolean pay(Player p, int amount) {
		return VaultHook.takeMoney(p, this.cost * amount);
	}

	@Override
	public void refund(Player p, int amount) {
		VaultHook.giveMoney(p, this.cost * amount);
	}

	@Override
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException {
		if (!config.contains(generatorId+".cost"))
			return false;
		
		if (!Main.getDependencies().isEnabled(Dependency.VAULT_ECONOMY))
			throw(new CannnotLoadUpgradeException("VAULT economy was not found!"));
		
		cost = config.getDouble(generatorId+".cost");
		return true;
	}

	@Override
	public String getCostFormatted(int amount) {
		return VaultHook.formatMoney(this.cost*amount);
	}
}
