package me.kryniowesegryderiusz.kgenerators.utils;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.kryniowesegryderiusz.kgenerators.Main;

/** {@link Sched} on Folia's region/entity/global/async schedulers. Only loaded on Folia. */
final class FoliaSched implements Sched.Backend {

	private static Sched.Task wrap(ScheduledTask task) {
		return task::cancel;
	}

	@Override
	public void global(Runnable task) {
		Bukkit.getGlobalRegionScheduler().execute(Main.getInstance(), task);
	}

	@Override
	public Sched.Task globalLater(Runnable task, long delayTicks) {
		return wrap(Bukkit.getGlobalRegionScheduler().runDelayed(Main.getInstance(), t -> task.run(), delayTicks));
	}

	@Override
	public Sched.Task globalTimer(Runnable task, long delayTicks, long periodTicks) {
		return wrap(Bukkit.getGlobalRegionScheduler().runAtFixedRate(Main.getInstance(), t -> task.run(), delayTicks, periodTicks));
	}

	@Override
	public Sched.Task async(Runnable task) {
		return wrap(Bukkit.getAsyncScheduler().runNow(Main.getInstance(), t -> task.run()));
	}

	@Override
	public Sched.Task asyncLater(Runnable task, long delayTicks) {
		return wrap(Bukkit.getAsyncScheduler().runDelayed(Main.getInstance(), t -> task.run(), delayTicks * 50L, TimeUnit.MILLISECONDS));
	}

	@Override
	public void at(Location location, Runnable task) {
		Bukkit.getRegionScheduler().execute(Main.getInstance(), location, task);
	}

	@Override
	public Sched.Task atLater(Location location, Runnable task, long delayTicks) {
		return wrap(Bukkit.getRegionScheduler().runDelayed(Main.getInstance(), location, t -> task.run(), delayTicks));
	}

	@Override
	public boolean ownsRegion(Location location) {
		return Bukkit.isOwnedByCurrentRegion(location);
	}

	@Override
	public void entity(Entity entity, Runnable task) {
		entity.getScheduler().run(Main.getInstance(), t -> task.run(), null);
	}

	@Override
	public void cancelAll() {
		Bukkit.getGlobalRegionScheduler().cancelTasks(Main.getInstance());
		Bukkit.getAsyncScheduler().cancelTasks(Main.getInstance());
	}
}
