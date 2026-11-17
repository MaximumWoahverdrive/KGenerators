package me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects;

import org.bukkit.entity.Player;

/**
 * An upgrade cost paid through another plugin (an economy or points plugin), which can refuse
 * the payment even after its requirements were checked. {@link Upgrade} pays these first, so
 * that a refused payment stops the upgrade before anything else is taken.
 */
public interface PaymentUpgradeCost {

	/**
	 * Takes the payment.
	 * @param p player who pays
	 * @param amount number of generators being upgraded
	 * @return whether the payment went through
	 */
	boolean pay(Player p, int amount);

	/**
	 * Gives back a payment made with {@link #pay(Player, int)}.
	 * @param p player who paid
	 * @param amount number of generators that were being upgraded
	 */
	void refund(Player p, int amount);
}
