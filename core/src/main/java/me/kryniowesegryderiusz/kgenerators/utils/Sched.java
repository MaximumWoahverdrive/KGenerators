package me.kryniowesegryderiusz.kgenerators.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.kryniowesegryderiusz.kgenerators.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.concurrent.TimeUnit;

/**
 * SWU: Folia-safe scheduling. Uses Paper's region/entity/global/async schedulers, which also
 * exist on plain Paper (where they map onto the main thread), so one code path serves both.
 * Rule: world/block/hologram work runs on the region owning the location, player/inventory
 * work on the player's scheduler, timers on the global region, database/IO async.
 */
public final class Sched {

	private Sched() {}

	/** next tick on the global region (replaces runTask without a location). */
	public static void global(Runnable task) {
		Bukkit.getGlobalRegionScheduler().execute(Main.getInstance(), task);
	}

	public static ScheduledTask globalLater(Runnable task, long delayTicks) {
		return Bukkit.getGlobalRegionScheduler().runDelayed(Main.getInstance(), t -> task.run(), Math.max(1L, delayTicks));
	}

	public static ScheduledTask globalTimer(Runnable task, long delayTicks, long periodTicks) {
		return Bukkit.getGlobalRegionScheduler().runAtFixedRate(Main.getInstance(), t -> task.run(), Math.max(1L, delayTicks), Math.max(1L, periodTicks));
	}

	public static ScheduledTask async(Runnable task) {
		return Bukkit.getAsyncScheduler().runNow(Main.getInstance(), t -> task.run());
	}

	public static ScheduledTask asyncLater(Runnable task, long delayTicks) {
		if (delayTicks <= 0) return async(task);
		return Bukkit.getAsyncScheduler().runDelayed(Main.getInstance(), t -> task.run(), delayTicks * 50L, TimeUnit.MILLISECONDS);
	}

	/** next tick on the region owning {@code location} (replaces runTask for world work - keeps the one-tick deferral). */
	public static void at(Location location, Runnable task) {
		Bukkit.getRegionScheduler().execute(Main.getInstance(), location, task);
	}

	public static ScheduledTask atLater(Location location, Runnable task, long delayTicks) {
		return Bukkit.getRegionScheduler().runDelayed(Main.getInstance(), location, t -> task.run(), Math.max(1L, delayTicks));
	}

	/** run now if this thread owns the location's region, else hand it to that region (replaces isPrimaryThread checks). */
	public static void atNow(Location location, Runnable task) {
		if (Bukkit.isOwnedByCurrentRegion(location)) task.run();
		else at(location, task);
	}

	/** next tick on the entity's scheduler (player inventories, GUIs). Dropped if the entity is gone. */
	public static void entity(Entity entity, Runnable task) {
		entity.getScheduler().run(Main.getInstance(), t -> task.run(), null);
	}

	public static void cancelAll() {
		Bukkit.getGlobalRegionScheduler().cancelTasks(Main.getInstance());
		Bukkit.getAsyncScheduler().cancelTasks(Main.getInstance());
	}
}
