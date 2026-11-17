package me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.LimitsManager;
import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.Text;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Limit {

    @Getter
    private final String id;

    @Getter
    private String name;

    @Getter
    private ItemStack item;

    @Getter
    private final ArrayList<Generator> generators = new ArrayList<>();

    @Getter
    private int placeLimit = -1;

    @Getter
    private boolean onlyOwnerPickUp = false;

    @Getter
    private boolean onlyOwnerUse = false;

    public Limit(LimitsManager limitsManager, Config config, String id) {
        boolean error = false;

        this.id = id;

        if (config.contains(id + ".name")) this.name = config.getString(id + ".name");
        else {
            Logger.error("Limits file: " + id + " doesnt have name set!");
            error = true;
        }

        if (config.contains(id + ".item"))
            this.item = ItemUtils.parseItemStack(config.getString(id + ".item"), "Limits file", false);
        else {
            Logger.error("Limits file: " + id + " doesnt have item set!");
            error = true;
        }

        if (config.contains(id + ".generators")) {
            List<?> gensIds = config.getList(id + ".generators");
            if (gensIds == null) {
                Logger.error("Limits file: " + id + " generators should be a list of generator ids!");
                gensIds = new ArrayList<>();
                error = true;
            }
            for (Object o : gensIds) {
                String s = String.valueOf(o);
                Generator g = Main.getGenerators().get(s);
                if (g != null) {
                    this.generators.add(g);
                } else {
                    Logger.error(
                            "Limits file: Cannot load generator " + s + " in " + id + ", because it doesnt exist!");
                    error = true;
                }
            }
        } else {
            if (id.equals("global_limits")) {
                this.generators.addAll(Main.getGenerators().getAll());
            } else {
                Logger.error("Limits file: " + id + " doesnt have generators set!");
                error = true;
            }
        }

        if (config.contains(id + ".place-limit")) this.placeLimit = config.getInt(id + ".place-limit");

        if (config.contains(id + ".only-owner-pickup"))
            this.onlyOwnerPickUp = config.getBoolean(id + ".only-owner-pickup");

        if (config.contains(id + ".only-owner-use")) this.onlyOwnerUse = config.getBoolean(id + ".only-owner-use");

        if (error) {
            Logger.error("Limits file: Couldnt load " + id + " limit!");
        } else {
            limitsManager.add(this);
            Logger.debugPluginLoad("Limits file: Loaded " + this);
        }
    }

    public boolean fulfillsPlaceLimit(GeneratorPlayer gp, GeneratorLocation gloc, PlayerLimits pLimits) {
        if (!generators.contains(gloc.getGenerator())) return true;
        return hasRoomFor(gp, gloc.getGenerator(), pLimits, gp.getOnlinePlayer());
    }

    /**
     * Checks whether changing a placed generator from {@code from} to {@code to} keeps the owner within
     * this limit. Only limits that count {@code to} but not {@code from} can be exceeded by it.
     *
     * @param gp owner of the generator
     * @param from generator before the upgrade
     * @param to generator after the upgrade
     * @param pLimits the owner's limits
     * @param notify who to tell if the limit is reached
     * @return true if this limit allows the change
     */
    public boolean fulfillsUpgradeLimit(
            GeneratorPlayer gp, Generator from, Generator to, PlayerLimits pLimits, CommandSender notify) {
        if (!generators.contains(to) || generators.contains(from)) return true;
        return hasRoomFor(gp, to, pLimits, notify);
    }

    private boolean hasRoomFor(GeneratorPlayer gp, Generator generator, PlayerLimits pLimits, CommandSender notify) {
        int playerLimit = pLimits.getLimit(this);
        if (playerLimit == -1 || getPlacedGenerators(gp) < playerLimit) return true;

        Lang.getMessageStorage()
                .send(
                        notify,
                        Message.GENERATORS_LIMITS_CANT_MORE,
                        "<number>",
                        String.valueOf(playerLimit),
                        "<generator>",
                        Text.name(generator.getGeneratorItem().getItemMeta()),
                        "<limit>",
                        this.name);
        return false;
    }

    public int getPlacedGenerators(GeneratorPlayer gp) {
        int amount = 0;
        for (Generator g : generators) {
            amount += gp.getGeneratorsCount(g);
        }
        return amount;
    }

    /**
     * Online player only
     *
     * @param gp player trying to pick up the generator
     * @param gLocation generator location being picked up
     * @return true if this limit allows the pick up; false (after messaging the player) otherwise
     */
    public Boolean fulfillsOnlyOwnerPickUp(GeneratorPlayer gp, GeneratorLocation gLocation) {
        if (this.onlyOwnerPickUp) return ownerCheck(gp, gLocation, Message.GENERATORS_LIMITS_CANT_PICK_UP);
        else return true;
    }

    public Boolean fulfillsOnlyOwnerUse(GeneratorPlayer gp, GeneratorLocation gLocation) {
        if (this.onlyOwnerUse) return ownerCheck(gp, gLocation, Message.GENERATORS_LIMITS_CANT_USE);
        else return true;
    }

    private boolean ownerCheck(GeneratorPlayer gp, GeneratorLocation gLocation, Message message) {
        if (!generators.contains(gLocation.getGenerator())) return true;

        Player player = gp.getOnlinePlayer();

        if (player.hasPermission("kgenerators.bypass.onlyownerchecks")) return true;

        /* To prevent errors if generator havent any owner */
        if (gLocation.getOwner().isNone()) return true;

        if (gp != gLocation.getOwner()) {
            Lang.getMessageStorage()
                    .send(player, message, "<owner>", gLocation.getOwner().getName());
            return false;
        }

        return true;
    }

    public String toString() {
        ArrayList<String> generators = new ArrayList<>();
        for (Generator g : this.generators) {
            generators.add(g.getId());
        }
        return String.format(
                "ID: %s | PlaceLimit: %s | OnlyOwnerUse: %s | OnlyOwnerPickup: %s | Affected generators: %s",
                this.id, this.placeLimit, this.onlyOwnerUse, this.onlyOwnerPickUp, generators);
    }
}
