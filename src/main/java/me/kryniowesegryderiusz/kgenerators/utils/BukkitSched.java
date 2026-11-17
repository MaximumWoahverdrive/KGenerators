package me.kryniowesegryderiusz.kgenerators.utils;

import me.kryniowesegryderiusz.kgenerators.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

/** {@link Sched} on the Bukkit scheduler: everything but Folia. */
final class BukkitSched implements Sched.Backend {

    private static BukkitScheduler scheduler() {
        return Bukkit.getScheduler();
    }

    private static Sched.Task wrap(BukkitTask task) {
        return task::cancel;
    }

    @Override
    public void global(Runnable task) {
        scheduler().runTask(Main.getInstance(), task);
    }

    @Override
    public Sched.Task globalLater(Runnable task, long delayTicks) {
        return wrap(scheduler().runTaskLater(Main.getInstance(), task, delayTicks));
    }

    @Override
    public Sched.Task globalTimer(Runnable task, long delayTicks, long periodTicks) {
        return wrap(scheduler().runTaskTimer(Main.getInstance(), task, delayTicks, periodTicks));
    }

    @Override
    public Sched.Task async(Runnable task) {
        return wrap(scheduler().runTaskAsynchronously(Main.getInstance(), task));
    }

    @Override
    public Sched.Task asyncLater(Runnable task, long delayTicks) {
        return wrap(scheduler().runTaskLaterAsynchronously(Main.getInstance(), task, delayTicks));
    }

    @Override
    public void at(Location location, Runnable task) {
        global(task);
    }

    @Override
    public Sched.Task atLater(Location location, Runnable task, long delayTicks) {
        return globalLater(task, delayTicks);
    }

    @Override
    public boolean ownsRegion(Location location) {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public void entity(Entity entity, Runnable task) {
        global(task);
    }

    @Override
    public void cancelAll() {
        scheduler().cancelTasks(Main.getInstance());
    }
}
