package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.api.events.AndroidMineEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ExplosiveToolBreakBlocksEvent;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;

public class SlimefunHook implements Listener {
	
	/*
	 * Handles android mining
	 * @param e
	 */
	@EventHandler
	public void onAndroidMineEvent (final AndroidMineEvent e){
		
		if (e.isCancelled())
			return;
		e.setCancelled(handler(e.getBlock().getLocation()));
	}
	
	/*
	 * Handles all explosion regeneration on SINGLE GeneratorType. If DOUBLE event is cancelled.
	 * @param e
	 */
	@EventHandler
	public void onExplosiveToolBreakBlocksEvent (final ExplosiveToolBreakBlocksEvent e){
		
		if (e.isCancelled())
			return;
		
		ArrayList<Block> blocks = new ArrayList<>(e.getAdditionalBlocks());
		blocks.add(e.getPrimaryBlock());
		
		boolean cancel = false;
		for (Block b : blocks)
		{
			if (handler(b.getLocation()))
				cancel = true;
		}
		if (cancel)
			Lang.getMessageStorage().send(e.getPlayer(), Message.HOOKS_EXPLODE_PICKAXE_CANNOT_USE_ON_DOUBLE);
		e.setCancelled(cancel);
	}
	
	/**
	 * Should event be cancelled? Schedules regeneration if the generated block was mined.
	 * @param l location mined
	 * @return true if the location is a generator block other than its generated block
	 */
	public static boolean handler(Location l)
	{
		GeneratorLocation gLocation = Main.getPlacedGenerators().getLoaded(l);
		if (gLocation != null) {
			if (gLocation.getGeneratedBlockLocation().equals(l))
				gLocation.scheduleGeneratorRegeneration();
			else return true;
		}
			
		return false;
	}

}
