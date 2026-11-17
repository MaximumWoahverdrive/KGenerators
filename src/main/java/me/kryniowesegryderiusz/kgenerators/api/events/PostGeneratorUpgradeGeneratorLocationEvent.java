package me.kryniowesegryderiusz.kgenerators.api.events;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.Upgrade;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event is fired after a placed generator is upgraded.
 * @author user
 */
public class PostGeneratorUpgradeGeneratorLocationEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Upgrade upgrade;

    @Getter
    private final Player player;

    @Getter
    private final IGeneratorLocation generatorLocation;

    public PostGeneratorUpgradeGeneratorLocationEvent(
            Upgrade upgrade, Player player, IGeneratorLocation generatorLocation) {
        this.upgrade = upgrade;
        this.generatorLocation = generatorLocation;
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
