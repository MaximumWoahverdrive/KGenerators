package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import me.kryniowesegryderiusz.kgenerators.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy econ = null;

    public static boolean setupEconomy() {
        if (Main.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp =
                Main.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static boolean takeMoney(Player player, Double cost) {
        EconomyResponse r = econ.withdrawPlayer(player, cost);

        return r.transactionSuccess();
    }

    public static void giveMoney(Player player, double amount) {
        econ.depositPlayer(player, amount);
    }

    public static String formatMoney(double money) {
        if (econ == null) return String.valueOf(money);
        return econ.format(money);
    }

    public static double getBalance(Player p) {
        return econ.getBalance(p);
    }
}
