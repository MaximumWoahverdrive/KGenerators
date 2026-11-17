package me.kryniowesegryderiusz.kgenerators.api.events;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GeneratorLoadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    IGeneratorLocation generatorLocation;

    public GeneratorLoadEvent(IGeneratorLocation generatorLocation) {
        this.generatorLocation = generatorLocation;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
