package me.kryniowesegryderiusz.kgenerators.generators.players.objects;

import java.util.HashMap;
import java.util.Map.Entry;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects.Limit;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects.PlayerLimits;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneratorPlayer {

    @Getter
    private boolean isNone = false;

    private final String nick;

    @Getter
    private volatile boolean loaded = false;

    public GeneratorPlayer(String nick) {
        this.nick = nick;
    }

    public GeneratorPlayer(String nick, boolean isNone) {
        this.nick = nick;
        this.isNone = isNone;
    }

    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(this.nick);
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.nick);
    }

    public String getName() {
        if (!isNone) return this.nick;
        else return Lang.getMessageStorage().get(Message.GENERATORS_ANY_OWNER_NONE, false);
    }

    /*
     * Limits
     */

    private final HashMap<String, Integer> playersGenerators = new HashMap<>();

    public synchronized void loadIfNeeded() {
        if (!this.loaded) loadPlayer();
    }

    public synchronized void loadPlayer() {

        for (GeneratorLocation gl : Main.getDatabases().getDb().getGenerators(nick)) {
            this.increaseGeneratorsAmount(gl.getGenerator());
        }
        this.loaded = true;
    }

    public synchronized void addGeneratorToPlayer(Generator generator) {
        if (isNone) return;

        if (!this.isLoaded()) this.loadPlayer();

        this.increaseGeneratorsAmount(generator);
    }

    private void increaseGeneratorsAmount(Generator generator) {
        if (playersGenerators.containsKey(generator.getId())) {
            int nr = playersGenerators.get(generator.getId());
            nr++;
            playersGenerators.put(generator.getId(), nr);
        } else {
            playersGenerators.put(generator.getId(), 1);
        }
    }

    public synchronized void removeGeneratorFromPlayer(Generator generator) {
        if (isNone) return;

        if (!this.isLoaded()) this.loadPlayer();

        if (playersGenerators.containsKey(generator.getId())) {
            int nr = playersGenerators.get(generator.getId());
            nr--;
            playersGenerators.put(generator.getId(), nr);
        }
    }

    public synchronized Integer getAllGeneratorsCount() {
        if (isNone) return 0;

        if (!this.isLoaded()) this.loadPlayer();

        int nr = 0;
        for (Entry<String, Integer> e : playersGenerators.entrySet()) {
            nr = nr + e.getValue();
        }
        return nr;
    }

    public synchronized Integer getGeneratorsCount(Generator generator) {
        if (isNone) return 0;

        if (!this.isLoaded()) this.loadPlayer();

        return playersGenerators.getOrDefault(generator.getId(), 0);
    }

    /**
     * Place limits only apply to an online owner; generators without an owner or spawned for an
     * offline one are not limited.
     *
     * @param gLoc generator location the player wants to place
     * @return true if no configured limit prevents placing it
     */
    public Boolean canPlace(GeneratorLocation gLoc) {
        if (!Main.getLimits().hasLimits() || isNone) return true;

        Player player = this.getOnlinePlayer();
        if (player == null) return true;

        PlayerLimits pl = new PlayerLimits(player);
        for (Limit limit : Main.getLimits().getValues()) {
            if (!limit.fulfillsPlaceLimit(this, gLoc, pl)) return false;
        }
        return true;
    }

    /**
     * Whether upgrading a placed generator of this owner to {@code next} stays within the place limits.
     * Like {@link #canPlace}, limits only apply to an online owner.
     *
     * @param gLoc generator location being upgraded
     * @param next generator it is upgraded to
     * @param notify who to tell if a limit is reached
     * @return true if no configured limit prevents the upgrade
     */
    public boolean canUpgrade(GeneratorLocation gLoc, Generator next, CommandSender notify) {
        if (!Main.getLimits().hasLimits() || isNone) return true;

        Player player = this.getOnlinePlayer();
        if (player == null) return true;

        PlayerLimits pl = new PlayerLimits(player);
        for (Limit limit : Main.getLimits().getValues()) {
            if (!limit.fulfillsUpgradeLimit(this, gLoc.getGenerator(), next, pl, notify)) return false;
        }
        return true;
    }

    /**
     * Online player only
     *
     * @param gLoc generator location the player wants to pick up
     * @return true if no configured limit prevents picking it up
     */
    public Boolean canPickUp(GeneratorLocation gLoc) {
        if (!Main.getLimits().hasLimits()) return true;

        for (Limit limit : Main.getLimits().getValues()) {
            if (!limit.fulfillsOnlyOwnerPickUp(this, gLoc)) return false;
        }
        return true;
    }

    public Boolean canUse(GeneratorLocation gLoc) {
        if (!Main.getLimits().hasLimits()) return true;

        for (Limit limit : Main.getLimits().getValues()) {
            if (!limit.fulfillsOnlyOwnerUse(this, gLoc)) return false;
        }
        return true;
    }
}
