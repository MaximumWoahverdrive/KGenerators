package me.kryniowesegryderiusz.kgenerators.api.events;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.Upgrade;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event is fired, when generator is being removed from world.
 * @author user
 */
public class PostGeneratorUpgradeItemEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Upgrade upgrade;

    @Getter
    private final Player player;

    @Getter
    private final ItemStack item;

    public PostGeneratorUpgradeItemEvent(Upgrade upgrade, Player p, ItemStack item) {
        this.upgrade = upgrade;
        this.player = p;
        this.item = item;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
