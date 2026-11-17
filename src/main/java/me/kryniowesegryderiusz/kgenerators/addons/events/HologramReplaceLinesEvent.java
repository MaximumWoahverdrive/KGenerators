package me.kryniowesegryderiusz.kgenerators.addons.events;

import java.util.HashMap;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IHologramText;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HologramReplaceLinesEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final IHologramText hologram;

    @Getter
    private final GeneratorLocation generatorLocation;

    @Getter
    private final HashMap<String, String> replacablesMap = new HashMap<>();

    public HologramReplaceLinesEvent(GeneratorLocation generatorLocation, IHologramText hologram) {
        this.generatorLocation = generatorLocation;
        this.hologram = hologram;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
