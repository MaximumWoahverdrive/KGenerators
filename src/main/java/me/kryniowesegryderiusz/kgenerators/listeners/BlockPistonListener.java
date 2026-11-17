package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.ArrayList;
import java.util.List;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class BlockPistonListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonExtend(final BlockPistonExtendEvent e) {
        try {
            if (pistonEvent(e.getBlocks())) e.setCancelled(true);
        } catch (Exception exception) {
            Logger.error(exception);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonRetractEvent(final BlockPistonRetractEvent e) {
        try {
            if (pistonEvent(e.getBlocks())) e.setCancelled(true);
        } catch (Exception exception) {
            Logger.error(exception);
        }
    }

    /* Returns true if cancelled. Regeneration is only scheduled once every block is known to be allowed. */
    Boolean pistonEvent(List<Block> blocks) {
        List<GeneratorLocation> pushed = new ArrayList<>();
        for (Block block : blocks) {
            GeneratorLocation gLoc = Main.getPlacedGenerators().getLoaded(block.getLocation());
            if (gLoc != null) {
                if (!gLoc.getGenerator().isAllowPistonPush()
                        || !gLoc.getGeneratedBlockLocation().equals(block.getLocation())
                        || Main.getSchedules().isScheduled(gLoc)
                        || Main.getLimits().isOnlyOwnerUse(gLoc.getGenerator())) {
                    return true;
                }
                pushed.add(gLoc);
            }
        }
        for (GeneratorLocation gLoc : pushed) gLoc.scheduleGeneratorRegeneration();
        return false;
    }
}
