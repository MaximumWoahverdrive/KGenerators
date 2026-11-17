package me.kryniowesegryderiusz.kgenerators.data;

import java.io.File;
import java.util.ArrayList;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.data.databases.IDatabase;
import me.kryniowesegryderiusz.kgenerators.data.databases.SQLDatabase;
import me.kryniowesegryderiusz.kgenerators.data.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.Sched;
import org.jetbrains.annotations.Nullable;

public class DatabaseManager {

    @Getter
    private IDatabase db;

    @Getter
    volatile boolean isMigratorRunning = false;

    public DatabaseManager(DatabaseType dbType) {
        Logger.debugPluginLoad("DatabaseManager: Setting up manager");
        this.db = this.getDatabase(dbType);
    }

    /**
     * Gets proper database or null if OTHER
     *
     * @param dbType type of database to create
     * @return new SQL database for MYSQL or SQLITE, or null for any other type
     */
    @Nullable
    public IDatabase getDatabase(DatabaseType dbType) {
        return switch (dbType) {
            case MYSQL -> new SQLDatabase(DatabaseType.MYSQL, Main.getSettings().getSqlConfig());
            case SQLITE ->
                new SQLDatabase(DatabaseType.SQLITE, Main.getSettings().getSqlConfig());
            default -> {
                Logger.error("DatabasesHandler: Could not return OTHER database, as not specified");
                yield null;
            }
        };
    }

    private MigratorInfo migratorInfo = null;

    public void changeTo(DatabaseType toDbType) {

        Sched.async(() -> {
            Logger.info("DatabasesHandler: Converting database from "
                    + Main.getSettings().getDbType().name() + " to " + toDbType.name() + ". IT COULD TAKE A WHILE!");

            IDatabase newDb = getDatabase(toDbType);

            if (newDb == null) {
                Logger.error("DatabasesHandler: Cannot change database to " + toDbType
                        + "! Connection could not be initialised!");
                return;
            }

            isMigratorRunning = true;
            try {
                int amount = db.getGeneratorsAmount();
                migratorInfo = new MigratorInfo(amount);

                int amountAtOnce = 500;

                for (int i = 0; i <= amount; i = i + amountAtOnce) {
                    Logger.info("DatabasesHandler: Migrating next " + amountAtOnce + " generators: " + i + "-"
                            + (i + amountAtOnce));
                    ArrayList<GeneratorLocation> next = db.getGenerators(i, amountAtOnce);
                    if (next == null) {
                        Logger.error("DatabasesHandler: Cannot read generators from the old database! Conversion"
                                + " stopped; still using " + Main.getSettings().getDbType() + ".");
                        return;
                    }
                    Logger.info("DatabaseHandler: Obtained " + next.size() + " from old database");
                    for (GeneratorLocation gl : next) {
                        newDb.saveGenerator(gl, true);
                        migratorInfo.increment();
                    }
                }

                Main.getSettings().setDbType(toDbType);
                FilesUtils.changeText(
                        new File(Main.getInstance().getDataFolder(), "config.yml"),
                        "  dbtype: ",
                        "  dbtype: " + toDbType.name() + "  #Earlier: ");

                db = newDb;
            } finally {
                if (migratorInfo != null) migratorInfo.end();
                migratorInfo = null;
                isMigratorRunning = false;
            }
            Logger.info("DatabasesHandler: Succesfully converted database to " + toDbType + "!");
        });
    }

    static class MigratorInfo {

        int amount;
        int migratedAmount = 0;
        Sched.Task taskId;

        public MigratorInfo(int generatorsAmount) {
            amount = generatorsAmount;

            taskId = Sched.globalTimer(
                    () -> Logger.info("DatabasesHandler: Migrated " + migratedAmount + "/" + amount), 40L, 40L);
        }

        public void increment() {
            this.migratedAmount++;
        }

        public void end() {
            taskId.cancel();
            Logger.info("DatabasesHandler: Migrated " + this.migratedAmount + "/" + this.amount);
        }
    }
}
