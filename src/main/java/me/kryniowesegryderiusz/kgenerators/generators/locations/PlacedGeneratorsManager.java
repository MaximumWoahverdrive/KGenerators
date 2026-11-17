package me.kryniowesegryderiusz.kgenerators.generators.locations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.GeneratorLoadEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.GeneratorUnloadEvent;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.objects.Schedule;
import me.kryniowesegryderiusz.kgenerators.listeners.ChunkLoadListener;
import me.kryniowesegryderiusz.kgenerators.listeners.ChunkUnloadListener;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.Sched;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class PlacedGeneratorsManager {

    @Getter
    private final ConcurrentHashMap<ChunkInfo, ChunkGeneratorLocations> loadedGenerators = new ConcurrentHashMap<>();

    private Sched.Task managementTask;

    @Getter
    private final LinkedBlockingQueue<ManagementTask> managementQueue = new LinkedBlockingQueue<>();

    private volatile boolean stopping = false;

    public PlacedGeneratorsManager() {

        Logger.debugPluginLoad("PlacedGenerators: Starting chunk management task");

        nextManagementTick();

        Logger.debugPluginLoad("PlacedGenerators: Loading generators from already loaded chunks");

        HashMap<World, Chunk[]> loadedChunks = new HashMap<>();

        Main.getInstance().getServer().getPluginManager().registerEvents(new ChunkLoadListener(), Main.getInstance());
        Main.getInstance().getServer().getPluginManager().registerEvents(new ChunkUnloadListener(), Main.getInstance());

        for (World w : Bukkit.getWorlds()) {
            loadedChunks.put(w, w.getLoadedChunks());
        }

        for (Entry<World, Chunk[]> e : loadedChunks.entrySet()) {
            int chunks = 0;
            for (Chunk c : e.getValue()) {
                loadChunk(c);
                chunks++;
            }
            Logger.debugPluginLoad("PlacedGenerators: Queued " + chunks + " loaded chunks of world "
                    + e.getKey().getName() + " for loading generators");
        }
    }

    private void nextManagementTick() {
        managementTask = Sched.asyncLater(
                () -> {
                    while (!managementQueue.isEmpty()) {
                        if (stopping) break;
                        ManagementTask r = managementQueue.poll();
                        if (r != null) {
                            try {
                                r.doTask();
                            } catch (Exception e) {
                                Logger.error(e);
                            }
                        } else break;
                    }
                    if (!stopping) nextManagementTick();
                },
                1L);
    }

    public void onDisable() {
        stopping = true;
        managementTask.cancel();
        while (!managementQueue.isEmpty()) {
            ManagementTask r = managementQueue.poll();
            if (r != null) {
                // One failing task must not stop the generators and schedules below from being saved
                try {
                    r.doTask();
                } catch (Exception e) {
                    Logger.error(e);
                }
            } else break;
        }
        Main.getSchedules().unloadAllSchedules();
        for (GeneratorLocation gl : this.getAll()) {
            Main.getDatabases().getDb().saveGenerator(gl);
        }
    }

    public void loadChunk(Chunk c) {
        managementQueue.add(new ChunkLoadTask(c));
    }

    public void unloadChunk(Chunk c) {
        managementQueue.add(new ChunkUnloadTask(getChunkInfo(c)));
    }

    /**
     * Unloads every generator in {@code world}. A world can be unloaded without its chunks
     * getting their own unload events, which would leave its generators loaded (and their
     * regenerations scheduled) against a world that is gone.
     * @param world world whose generators should be unloaded
     */
    public void unloadWorld(World world) {
        for (ChunkInfo ci : loadedGenerators.keySet()) {
            if (ci.getWorld().equals(world)) managementQueue.add(new ChunkUnloadTask(ci));
        }
    }

    public abstract static class ManagementTask {
        public abstract void doTask();
    }

    public class ChunkLoadTask extends ManagementTask {

        private final ChunkInfo ci;
        private final long taskScheduleTime = System.currentTimeMillis();

        public ChunkLoadTask(Chunk c) {
            this.ci = getChunkInfo(c);
            Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Scheduling chunk load: " + ci);
        }

        @Override
        public void doTask() {
            long timeTaken = System.currentTimeMillis() - taskScheduleTime;
            Logger.debugPlacedGeneratorsManager(
                    "PlacedGeneratorsManager: Loading chunk: " + ci + " (After " + timeTaken + "ms)");
            if (timeTaken > 500) {
                Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Waiting for chunk load " + ci
                        + " took more than 500ms! (" + timeTaken + "ms) Is the server overloaded?");
            }

            long taskTime = System.currentTimeMillis();

            ArrayList<GeneratorLocation> generators =
                    Main.getDatabases().getDb().getGenerators(ci);

            if (generators == null) {
                Logger.error("PlacedGeneratorsManager: Cant load chunk " + ci + "! Trying again!");
                managementQueue.add(this);
                return;
            }

            Main.getPlacedGenerators().getLoadedGenerators().putIfAbsent(ci, new ChunkGeneratorLocations());

            for (GeneratorLocation gl : generators) {

                Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Loading generator: " + gl);

                Schedule finalSchedule = Main.getDatabases().getDb().getSchedule(gl);

                Main.getPlacedGenerators().addLoaded(gl);

                if (finalSchedule != null) {
                    Logger.debugSchedulesManager("PlacedGeneratorsManager: Loading schedule " + gl);
                    Main.getSchedules().getSchedules().put(gl, finalSchedule);
                    if (gl.getGenerator().isHologram()) Main.getHolograms().createRemainingTimeHologram(gl);
                    Main.getDatabases().getDb().removeSchedule(gl);
                } else if (!stopping) {
                    // SWU (Folia): reading the block must happen on the owning region, not this async worker
                    Sched.at(gl.getLocation(), () -> {
                        if (gl.isBroken()) {
                            gl.scheduleGeneratorRegeneration();
                            Logger.debugPlacedGeneratorsManager(
                                    "PlacedGeneratorsManager: Broken generator found on chunk load. Automatically fixing it: "
                                            + gl);
                        }
                    });
                }

                // Tasks can't be scheduled while the plugin is disabling
                if (!stopping)
                    Sched.at(
                            gl.getLocation(),
                            () -> Main.getInstance()
                                    .getServer()
                                    .getPluginManager()
                                    .callEvent(new GeneratorLoadEvent(gl)));
            }

            loadedGenerators.get(ci).setFullyLoaded(true);

            long tasktimeTaken = System.currentTimeMillis() - taskTime;
            Logger.debugPlacedGeneratorsManager(
                    "PlacedGeneratorsManager: Loading chunk " + ci + " taken " + tasktimeTaken + "ms)");

            Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Chunk loaded: " + ci);
        }
    }

    public class ChunkUnloadTask extends ManagementTask {

        private final ChunkInfo ci;
        private final long taskScheduleTime = System.currentTimeMillis();

        public ChunkUnloadTask(ChunkInfo ci) {
            this.ci = ci;

            Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Scheduling chunk unload: " + ci);
        }

        @Override
        public void doTask() {
            long timeTaken = System.currentTimeMillis() - taskScheduleTime;
            Logger.debugPlacedGeneratorsManager(
                    "PlacedGeneratorsManager: Unloading chunk: " + ci + " (After " + timeTaken + "ms)");
            if (timeTaken > 500) {
                Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Waiting for chunk unload " + ci
                        + " took more than 500ms! (" + timeTaken + "ms) Is the server overloaded?");
            }

            long taskTime = System.currentTimeMillis();

            ArrayList<GeneratorLocation> generatorsToUnload = getLoaded(ci);

            for (GeneratorLocation gl : generatorsToUnload) {

                Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Unloading generator: " + gl);

                /*
                 * Database
                 */

                Main.getDatabases().getDb().saveGenerator(gl);

                Schedule schedule = Main.getSchedules().getSchedule(gl);
                if (schedule != null) {
                    Logger.debugSchedulesManager("PlacedGeneratorsManager: Unloading schedule " + gl);
                    Main.getDatabases().getDb().addSchedule(gl, schedule);
                }

                /*
                 * Game
                 */

                removeLoaded(gl);

                if (schedule != null) Main.getSchedules().remove(gl);

                if (!stopping)
                    Sched.at(
                            gl.getLocation(),
                            () -> Main.getInstance()
                                    .getServer()
                                    .getPluginManager()
                                    .callEvent(new GeneratorUnloadEvent(gl)));
            }

            if (loadedGenerators.get(ci) != null && loadedGenerators.get(ci).isEmpty()) loadedGenerators.remove(ci);

            long tasktimeTaken = System.currentTimeMillis() - taskTime;
            Logger.debugPlacedGeneratorsManager(
                    "PlacedGeneratorsManager: Unloading chunk " + ci + " taken " + tasktimeTaken + "ms)");

            Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Chunk unloaded: " + ci + " | isInManager: "
                    + loadedGenerators.containsKey(ci));
        }
    }

    /**
     * Doesn't load or touch the chunk, so it's safe from any thread.
     * @param loc location inside the chunk to check
     * @return true if the chunk's generators are in the manager and fully loaded
     */
    public boolean isChunkFullyLoaded(Location loc) {
        ChunkGeneratorLocations cgl = loadedGenerators.get(chunkInfoAt(loc));
        boolean result = cgl != null && cgl.isFullyLoaded();
        Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: isChunkFullyLoaded fired with " + result);
        return result;
    }

    public ArrayList<GeneratorLocation> getAll() {
        ArrayList<GeneratorLocation> all = new ArrayList<>();
        for (ChunkGeneratorLocations c : this.loadedGenerators.values()) all.addAll(c.getAll());
        return all;
    }

    public void removeLoaded(GeneratorLocation gLocation) {
        if (loadedGenerators.containsKey(gLocation.getChunkInfo()))
            loadedGenerators.get(gLocation.getChunkInfo()).removeLocation(gLocation);
    }

    public void addLoaded(GeneratorLocation gLocation) {
        loadedGenerators.putIfAbsent(gLocation.getChunkInfo(), new ChunkGeneratorLocations());
        loadedGenerators.get(gLocation.getChunkInfo()).addLocation(gLocation);
    }

    /*
     * Manager related methods
     */

    /**
     * Doesn't load or touch the chunk, so it's safe from any thread.
     * @param location block location of the generator
     * @return loaded GeneratorLocation related to location or null if none
     */
    @Nullable
    public GeneratorLocation getLoaded(Location location) {
        ChunkGeneratorLocations cgl = loadedGenerators.get(chunkInfoAt(location));
        if (cgl != null) return cgl.get(location);
        else return null;
    }

    /**
     * Gets the loaded generators in a chunk.
     * @param ci chunk to look up
     * @return loaded GeneratorLocation list of that chunk, or an empty list if none
     */
    public ArrayList<GeneratorLocation> getLoaded(ChunkInfo ci) {
        ChunkGeneratorLocations cgl = this.loadedGenerators.get(ci);
        if (cgl != null) {
            return cgl.getAll();
        } else return new ArrayList<>();
    }

    /**
     * Checks whether a generator at the given location is loaded.
     * @param loc block location to check
     * @return whether GeneratorLocation related to location is loaded
     */
    public boolean isLoaded(Location loc) {
        return this.getLoaded(loc) != null;
    }

    /**
     * Checks whether the given generator is loaded.
     * @param gLoc generator location to check
     * @return whether GeneratorLocation is loaded
     */
    public boolean isLoaded(GeneratorLocation gLoc) {
        return isLoaded(gLoc.getLocation());
    }

    public int getLoadedGeneratorsAmount() {
        int amount = 0;
        for (ChunkGeneratorLocations chl : loadedGenerators.values()) {
            amount += chl.locations.size();
        }
        return amount;
    }

    /*
     * Bulk removal
     */

    public void bulkRemoveGenerators(
            World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean dropGenerator) {
        Sched.async(() -> {
            ArrayList<GeneratorLocation> generatorLocationsToRemove =
                    Main.getDatabases().getDb().getGenerators(world, minX, minY, minZ, maxX, maxY, maxZ);
            for (GeneratorLocation gloc : generatorLocationsToRemove) {
                Sched.at(gloc.getLocation(), () -> {
                    Logger.info("Bulk generator removal: " + gloc);
                    gloc.removeGenerator(dropGenerator, null);
                });
            }
        });
    }

    /*
     * Converters
     */

    public String locationToString(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        String world = location.getWorld().getName();
        return world + "," + x + "," + y + "," + z;
    }

    public Location stringToLocation(String string) {
        String[] parts = string.split(",");

        final World w = Bukkit.getServer().getWorld(parts[0]);
        final int x = Integer.parseInt(parts[1]);
        final int y = Integer.parseInt(parts[2]);
        final int z = Integer.parseInt(parts[3]);

        return new Location(w, x, y, z);
    }

    public ChunkInfo stringToChunkInfo(String string) {
        String[] parts = string.split(",");

        final World w = Bukkit.getServer().getWorld(parts[0]);
        final int x = Integer.parseInt(parts[1]);
        final int z = Integer.parseInt(parts[2]);

        return new ChunkInfo(w, x, z);
    }

    /*
     * ChunkGeneratorLocations class
     */

    public static class ChunkGeneratorLocations {

        @Getter
        @Setter
        private boolean fullyLoaded = false;

        private final ConcurrentHashMap<Location, GeneratorLocation> locations = new ConcurrentHashMap<>();

        public void addLocation(GeneratorLocation gLocation) {
            this.locations.put(gLocation.getLocation(), gLocation);
        }

        public void removeLocation(GeneratorLocation gLocation) {
            this.locations.remove(gLocation.getLocation());
        }

        public GeneratorLocation get(Location location) {

            if (location == null) return null;

            GeneratorLocation gl = locations.get(location);
            if (gl != null) return gl;

            gl = locations.get(location.clone().add(0, -1, 0));
            if (gl != null && gl.getGenerator().getType() == GeneratorType.DOUBLE) return gl;

            return null;
        }

        public ArrayList<GeneratorLocation> getAll() {
            return new ArrayList<>(this.locations.values());
        }

        public boolean isEmpty() {
            return locations.isEmpty();
        }
    }

    public ChunkInfo getChunkInfo(Chunk c) {
        return new ChunkInfo(c);
    }

    /** The chunk of a location, from its coordinates: unlike {@code getChunk()} this never loads the chunk. */
    private ChunkInfo chunkInfoAt(Location loc) {
        return new ChunkInfo(loc.getWorld(), loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    @AllArgsConstructor
    public class ChunkInfo {

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + Objects.hash(world, x, z);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            ChunkInfo other = (ChunkInfo) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance())) return false;
            return Objects.equals(world, other.world) && x == other.x && z == other.z;
        }

        @Getter
        World world;

        @Getter
        int x;

        @Getter
        int z;

        public ChunkInfo(Chunk c) {
            this.x = c.getX();
            this.z = c.getZ();
            this.world = c.getWorld();
        }

        @Override
        public String toString() {
            return "ChunkInfo: " + world.getName() + ", " + x + ", " + z;
        }

        private PlacedGeneratorsManager getEnclosingInstance() {
            return PlacedGeneratorsManager.this;
        }
    }
}
