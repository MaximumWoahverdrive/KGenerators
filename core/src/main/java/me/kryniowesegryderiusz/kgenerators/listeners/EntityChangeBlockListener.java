package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

/**
 * Stops gravity-affected generators from falling out of position.
 *
 * <p>A generator whose block obeys gravity -- sand, red sand, gravel, anvils,
 * concrete powder, dripstone -- turns into a {@link FallingBlock} entity as soon
 * as the space below it is clear. The entity lands somewhere else, but the
 * plugin's record of the generator stays at the original coordinates, leaving a
 * generator that cannot be mined, picked up or regenerated: a ghost.
 *
 * <p>Cancelling the conversion keeps the block where the plugin thinks it is.
 * Floating sand is the intended outcome here -- a generator is a placed machine,
 * not terrain.
 */
public class EntityChangeBlockListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onEntityChangeBlock(final EntityChangeBlockEvent e) {
		try {
			if (!(e.getEntity() instanceof FallingBlock))
				return;

			/*
			 * FallingBlockEntity fires this event twice, and only one of them
			 * matters here:
			 *
			 *   fall()  - the block becomes an entity, so the block is replaced
			 *             by air (or water, if it was waterlogged). This is the
			 *             one that strands a generator.
			 *   tick()  - the entity lands and becomes a block again, so `to` is
			 *             a solid material. Cancelling that would stop unrelated
			 *             falling blocks from ever landing.
			 */
			Material to = e.getTo();
			if (to != Material.AIR && to != Material.WATER)
				return;

			/*
			 * getLoaded() resolves both the generator block itself and, for
			 * DOUBLE generators, the generated block above it. Both are worth
			 * protecting: if a generated falling block drifts away there is no
			 * break event, so the generator would never schedule a regeneration.
			 */
			GeneratorLocation gLoc = Main.getPlacedGenerators().getLoaded(e.getBlock().getLocation());
			if (gLoc == null)
				return;

			e.setCancelled(true);
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}
}
