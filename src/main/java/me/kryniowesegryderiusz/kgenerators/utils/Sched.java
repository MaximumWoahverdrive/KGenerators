package me.kryniowesegryderiusz.kgenerators.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Scheduling that works on every supported server, from Paper 1.8 to Folia.
 *
 * <p>On Folia, work runs on Paper's region/entity/global/async schedulers: world, block and hologram
 * work on the region owning the location, player and inventory work on the player's scheduler,
 * timers on the global region, database and IO async. Everywhere else it runs on the Bukkit
 * scheduler exactly as before Folia support, so the region arguments are only hints there.
 *
 * <p>The Folia backend is a separate class that is never loaded off Folia, because the Paper
 * scheduler types it uses do not exist on older servers.
 */
public final class Sched {

	/** A scheduled task that can be cancelled. */
	public interface Task {
		void cancel();
	}

	interface Backend {
		void global(Runnable task);
		Task globalLater(Runnable task, long delayTicks);
		Task globalTimer(Runnable task, long delayTicks, long periodTicks);
		Task async(Runnable task);
		Task asyncLater(Runnable task, long delayTicks);
		void at(Location location, Runnable task);
		Task atLater(Location location, Runnable task, long delayTicks);
		boolean ownsRegion(Location location);
		void entity(Entity entity, Runnable task);
		void cancelAll();
	}

	private static final Backend BACKEND = isFolia() ? new FoliaSched() : new BukkitSched();

	private Sched() {}

	public static boolean isFolia() {
		try {
			Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Next tick, on the global region (replaces runTask without a location).
	 * @param task task to run
	 */
	public static void global(Runnable task) {
		BACKEND.global(task);
	}

	public static Task globalLater(Runnable task, long delayTicks) {
		return BACKEND.globalLater(task, Math.max(1L, delayTicks));
	}

	public static Task globalTimer(Runnable task, long delayTicks, long periodTicks) {
		return BACKEND.globalTimer(task, Math.max(1L, delayTicks), Math.max(1L, periodTicks));
	}

	public static Task async(Runnable task) {
		return BACKEND.async(task);
	}

	public static Task asyncLater(Runnable task, long delayTicks) {
		if (delayTicks <= 0) return async(task);
		return BACKEND.asyncLater(task, delayTicks);
	}

	/**
	 * Next tick, on the region owning {@code location} (replaces runTask for world work - keeps the one-tick deferral).
	 * @param location location whose region runs the task
	 * @param task task to run
	 */
	public static void at(Location location, Runnable task) {
		BACKEND.at(location, task);
	}

	public static Task atLater(Location location, Runnable task, long delayTicks) {
		return BACKEND.atLater(location, task, Math.max(1L, delayTicks));
	}

	/**
	 * Run now if this thread owns the location's region, else hand it to that region (replaces isPrimaryThread checks).
	 * @param location location whose region must run the task
	 * @param task task to run
	 */
	public static void atNow(Location location, Runnable task) {
		if (BACKEND.ownsRegion(location)) task.run();
		else at(location, task);
	}

	/**
	 * Next tick, on the entity's scheduler (player inventories, GUIs). On Folia, dropped if the entity is gone.
	 * @param entity entity whose scheduler runs the task
	 * @param task task to run
	 */
	public static void entity(Entity entity, Runnable task) {
		BACKEND.entity(entity, task);
	}

	public static void cancelAll() {
		BACKEND.cancelAll();
	}
}
