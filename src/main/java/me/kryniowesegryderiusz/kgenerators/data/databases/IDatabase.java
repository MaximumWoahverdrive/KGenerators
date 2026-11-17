package me.kryniowesegryderiusz.kgenerators.data.databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import me.kryniowesegryderiusz.kgenerators.generators.locations.PlacedGeneratorsManager.ChunkInfo;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.objects.Schedule;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public interface IDatabase {

    /**
     * Closes database connection
     */
    void closeConnection();

    void updateTable();

    /*
     * Closes connection objects;
     */
    void close(Statement stat, Connection conn, ResultSet res);

    /*
     * Placed generators table
     */

    /**
     * Gets the saved generator at the given block location
     * @param location block location of the generator
     * @return the generator at that location, or null if there is none
     */
    @Nullable
    GeneratorLocation getGenerator(Location location);

    /**
     * Gets all saved generators
     * @return list of all saved generators
     */
    ArrayList<GeneratorLocation> getGenerators();

    /**
     * Gets all saved generators
     * @param firstRow number of rows to skip before the first returned one
     * @param rowAmount maximum number of generators to return
     * @return list of saved generators in the requested row range
     */
    ArrayList<GeneratorLocation> getGenerators(int firstRow, int rowAmount);

    /**
     * Gets saved generators according to ranges
     * @param world world to search in
     * @param minX minimum block X coordinate (inclusive)
     * @param minY minimum block Y coordinate (inclusive)
     * @param minZ minimum block Z coordinate (inclusive)
     * @param maxX maximum block X coordinate (inclusive)
     * @param maxY maximum block Y coordinate (inclusive)
     * @param maxZ maximum block Z coordinate (inclusive)
     * @return list of saved generators inside the given box
     */
    ArrayList<GeneratorLocation> getGenerators(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ);

    /**
     * Gets saved generators according to owner
     * @param owner owner's player name
     * @return list of saved generators owned by that player
     */
    ArrayList<GeneratorLocation> getGenerators(String owner);

    /**
     * Gets saved generators according to chunk
     * return null if wasnt properly loaded
     * @param chunkInfo chunk to search in
     * @return list of saved generators in that chunk
     */
    @Nullable
    ArrayList<GeneratorLocation> getGenerators(ChunkInfo chunkInfo);

    /**
     * Gets saved generators amount
     * @return number of saved generators
     */
    int getGeneratorsAmount();

    /**
     * Saves generator location info to database
     * @param gl generator location to save
     */
    void saveGenerator(GeneratorLocation gl);

    /**
     * Saves generator location info to database
     * @param gl generator location to save
     * @param migrate if true, always insert it as a new row even if it already has an id
     */
    void saveGenerator(GeneratorLocation gl, boolean migrate);

    /**
     * Removes location from database
     * @param l block location of the generator to remove
     */
    void removeGenerator(Location l);

    /*
     * Scheduled generators table
     */

    void addSchedule(GeneratorLocation gl, Schedule schedule);

    Schedule getSchedule(GeneratorLocation gl);

    void removeSchedule(GeneratorLocation gl);
}
