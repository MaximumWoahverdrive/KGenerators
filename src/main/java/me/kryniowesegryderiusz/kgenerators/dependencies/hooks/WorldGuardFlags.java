package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.WGFlag;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * KGenerators' WorldGuard flags. Uses WorldGuard's classes, so it is only loaded when WorldGuard
 * is installed; everything else goes through {@link WorldGuardHook}.
 */
class WorldGuardFlags {

    public static StateFlag PICK_UP_FLAG;
    public static StateFlag ONLY_GEN_BREAK_FLAG;
    public static StateFlag INTERACT_FLAG;

    public boolean isWorldGuardHooked() {
        return PICK_UP_FLAG != null && ONLY_GEN_BREAK_FLAG != null && INTERACT_FLAG != null;
    }

    public void worldGuardFlagsAdd() {

        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

            if (registry == null) {
                return;
            }

            for (WGFlag eflag : WGFlag.values()) {
                Logger.info("WorldGuard: Registering " + eflag.getFlagId() + " flag");
                try {
                    StateFlag flag = new StateFlag(eflag.getFlagId(), eflag.getDefaultState());
                    registry.register(flag);
                    if (eflag == WGFlag.PICK_UP) PICK_UP_FLAG = flag;
                    if (eflag == WGFlag.ONLY_GEN_BREAK) ONLY_GEN_BREAK_FLAG = flag;
                    if (eflag == WGFlag.INTERACT) INTERACT_FLAG = flag;

                } catch (FlagConflictException e) {
                    Logger.error("WorldGuard: FlagConflictException!");
                    Flag<?> existing = registry.get(eflag.getFlagId());
                    if (existing instanceof StateFlag existingFlag) {
                        Logger.warn("WorldGuard: Overriding flag!");

                        if (eflag == WGFlag.PICK_UP) PICK_UP_FLAG = existingFlag;
                        if (eflag == WGFlag.ONLY_GEN_BREAK) ONLY_GEN_BREAK_FLAG = existingFlag;
                        if (eflag == WGFlag.INTERACT) INTERACT_FLAG = existingFlag;

                    } else {
                        Logger.error("WorldGuard: Flag overriding not possible! Types dont match!");
                    }
                }
            }
        } catch (IllegalStateException e) {
            Logger.error("WorldGuard: An error occurred, while adding WorldGuard flags!");
            Logger.error("WorldGuard: Are you using PlugMan? ;)");
        } catch (NoClassDefFoundError e) {
            Logger.error("WorldGuard: An error occurred, while adding WorldGuard flags!");
            Logger.error("WorldGuard: WorldGuard is installed, but didnt load properly!");
            Logger.error(e);
        }
    }

    /* Returns if pick up is allow/deny */
    public boolean isWorldGuardFlagAllow(Location location, Player player, WGFlag flag) {

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        StateFlag stateFlag = null;
        if (flag == WGFlag.PICK_UP) stateFlag = PICK_UP_FLAG;
        if (flag == WGFlag.ONLY_GEN_BREAK) stateFlag = ONLY_GEN_BREAK_FLAG;
        if (flag == WGFlag.INTERACT) stateFlag = INTERACT_FLAG;
        if (stateFlag == null) return false;

        // No region data for this world (regions disabled there): only the flag's default applies
        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (regions == null) return stateFlag.getDefault() == StateFlag.State.ALLOW;

        ApplicableRegionSet regionSet = regions.getApplicableRegions(
                BukkitAdapter.adapt(location).toVector().toBlockPoint());
        return regionSet.testState(WorldGuardPlugin.inst().wrapPlayer(player), stateFlag);
    }
}
