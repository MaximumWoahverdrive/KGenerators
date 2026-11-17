package me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects;

import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import org.bukkit.entity.Player;

public class UpgradeCostExp implements IUpgradeCost {

    public UpgradeCostExp() {}

    int cost;

    @Override
    public boolean checkRequirements(Player p, int amount) {
        return this.cost * amount <= currentExperience(p);
    }

    /**
     * Experience points the player has right now. Not {@code getTotalExperience()}: that counts all
     * experience ever collected and isn't lowered by enchanting or anvils.
     */
    static int currentExperience(Player p) {
        int level = p.getLevel();
        int levelPoints;
        if (level <= 16) levelPoints = level * level + 6 * level;
        else if (level <= 31) levelPoints = (int) (2.5 * level * level - 40.5 * level + 360);
        else levelPoints = (int) (4.5 * level * level - 162.5 * level + 2220);
        return levelPoints + Math.round(p.getExp() * p.getExpToLevel());
    }

    @Override
    public void takeRequirements(Player p, int amount) {
        p.giveExp(-this.cost * amount);
    }

    @Override
    public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException {
        if (!config.contains(generatorId + ".exp")) return false;

        cost = config.getInt(generatorId + ".exp");
        return true;
    }

    @Override
    public String getCostFormatted(int amount) {
        return amount * this.cost + " " + Lang.getMessageStorage().get(Message.UPGRADES_COST_EXP);
    }
}
