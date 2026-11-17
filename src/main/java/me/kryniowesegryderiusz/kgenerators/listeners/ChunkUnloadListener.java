package me.kryniowesegryderiusz.kgenerators.listeners;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class ChunkUnloadListener implements Listener {

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent e) {

        try {

            if (Main.getDatabases().isMigratorRunning()) return;

            Main.getPlacedGenerators().unloadChunk(e.getChunk());

        } catch (Exception exception) {
            Logger.error(exception);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(final WorldUnloadEvent e) {

        try {

            if (Main.getDatabases().isMigratorRunning()) return;

            Main.getPlacedGenerators().unloadWorld(e.getWorld());

        } catch (Exception exception) {
            Logger.error(exception);
        }
    }
}
