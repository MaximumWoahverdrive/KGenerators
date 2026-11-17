package me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces;

import java.util.ArrayList;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.utils.Sched;

public abstract class IHologramProvider {

    protected abstract void providerCreateHologram(GeneratorLocation gLocation, ArrayList<String> lines);

    protected abstract void providerUpdateHologram(GeneratorLocation gLocation, ArrayList<String> lines);

    protected abstract void providerRemoveHologram(GeneratorLocation gLocation);

    public void createHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
        Sched.atNow(gLocation.getLocation(), () -> providerCreateHologram(gLocation, lines));
    }

    public void updateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
        Sched.atNow(gLocation.getLocation(), () -> providerUpdateHologram(gLocation, lines));
    }

    public void removeHologram(GeneratorLocation gLocation) {
        Sched.atNow(gLocation.getLocation(), () -> providerRemoveHologram(gLocation));
    }
}
