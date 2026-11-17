package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.events.BarrelPlaceEvent;
import com.bgsoftware.wildstacker.api.events.BarrelPlaceInventoryEvent;
import com.bgsoftware.wildstacker.api.objects.StackedBarrel;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.utils.BlockUtils;
import me.kryniowesegryderiusz.kgenerators.utils.Sched;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class WildStackerHook implements Listener {

    public static void setup() {
        Main.getInstance().getServer().getPluginManager().registerEvents(new WildStackerHook(), Main.getInstance());
    }

    @EventHandler
    public void onBarrelPlaceInventory(BarrelPlaceInventoryEvent e) {
        for (Generator g : Main.getGenerators().getAll()) {
            if (e.getPlayer().getInventory().containsAtLeast(g.getGeneratorItem(), 1)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBarrelPlace(BarrelPlaceEvent e) {
        Generator g = Main.getGenerators().get(e.getItemInHand());
        if (g != null) {
            e.setCancelled(true);
            if (g.getType() == GeneratorType.DOUBLE) {
                Sched.atLater(
                        e.getBarrel().getLocation(),
                        () -> BlockUtils.setBlock(e.getBarrel().getLocation(), g.getGeneratorItem()),
                        1);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent e) {

        if (Main.getGenerators().get(e.getItemInHand()) != null) {

            int cx = e.getBlock().getChunk().getX();
            int cz = e.getBlock().getChunk().getZ();

            for (int x = cx - 1; x <= cx + 1; x++) {
                for (int z = cz - 1; z <= cz + 1; z++) {
                    // Barrels in unloaded chunks can't be merged into; don't load them just to look
                    if (!e.getBlock().getWorld().isChunkLoaded(x, z)) continue;
                    for (StackedBarrel b : WildStackerAPI.getWildStacker()
                            .getSystemManager()
                            .getStackedBarrels(
                                    e.getBlock().getLocation().getWorld().getChunkAt(x, z))) {
                        if (e.getBlock().getLocation().distance(b.getLocation()) <= b.getMergeRadius() + 1) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}
