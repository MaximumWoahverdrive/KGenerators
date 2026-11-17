package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.waterarchery.litminions.api.events.MinerMinionBreakEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class LitMinionsHook implements Listener {

    @EventHandler
    public void MinionBreakEvent(final MinerMinionBreakEvent e) {
        Location location = e.getBlock().getLocation();

        if (!Main.getPlacedGenerators().isChunkFullyLoaded(location)) {
            e.setCancelled(true);
            return;
        }

        GeneratorLocation gLocation = Main.getPlacedGenerators().getLoaded(location);
        if (gLocation != null) {
            if (!gLocation.isBlockPossibleToMine(location)) e.setCancelled(true);
            else {
                if (gLocation.getLastGeneratedObject() != null
                        && gLocation.getLastGeneratedObject().getCustomDrops() != null) {
                    if (gLocation.getLastGeneratedObject().getCustomDrops().isRemoveDefaults())
                        e.getDrops().clear();
                    ItemStack drop =
                            gLocation.getLastGeneratedObject().getCustomDrops().getItem();
                    if (drop != null) e.getDrops().add(drop.clone());
                    e.getMinion()
                            .setExp(e.getMinion().getExp()
                                    + gLocation
                                            .getLastGeneratedObject()
                                            .getCustomDrops()
                                            .getExp());
                    gLocation
                            .getLastGeneratedObject()
                            .getCustomDrops()
                            .doCommandDrops(e.getMinion().getOwnerName(), location);
                }
                gLocation.scheduleGeneratorRegeneration();
            }
        }
    }
}
