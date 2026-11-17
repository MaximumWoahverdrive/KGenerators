package me.kryniowesegryderiusz.kgenerators.api;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * API version v7.PRE If you lack API functions and need to get access of other
 * parts of KGenerators feel free to contact me!
 *
 * @author Kryniowesegryderiusz
 */
public class KGeneratorsAPI {
    public KGeneratorsAPI() {}

    /**
     * Get IGeneratorLocation by location. It gives access to all generator-related
     * methods
     * <p>
     * Note that it could be upper block of double generator!
     * <p>
     * See
     * {@link me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation}
     * for more information
     *
     * @param location Location
     * @return IGeneratorLocation or null if not loaded
     */
    public static @Nullable IGeneratorLocation getLoadedGeneratorLocation(Location location) {
        return Main.getPlacedGenerators().getLoaded(location);
    }

    /**
     * Registers and loads new upgrade cost. It should implement IUpgradeCost
     * interface Ex.
     * {@link me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.UpgradeCostMoney}
     *
     * @param <T> upgrade cost type
     * @param c class being registered
     * @since 6.0.0
     */
    public static <T extends IUpgradeCost> void registerUpgradeCost(Class<T> c) {
        Main.getUpgrades().getUpgradesCostsManager().registerUpgradeCost(c);
        Main.getUpgrades().reload();
    }

    /**
     * Registers and loads new generated object. It should extend
     * AbstractGeneratedObject class Ex.
     * {@link me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratedBlock}
     *
     * @param <T> generated object type
     * @param c class being registered
     * @since 6.0.0
     */
    public static <T extends AbstractGeneratedObject> void registerGeneratedObjec(Class<T> c) {
        Main.getGenerators().getGeneratedObjectsManager().registerGeneratedObject(c);
        Main.getGenerators().reload();
    }
}
