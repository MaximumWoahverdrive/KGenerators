package me.kryniowesegryderiusz.kgenerators.logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.addons.Addons;
import me.kryniowesegryderiusz.kgenerators.addons.objects.Addon;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.enums.LogType;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.Sched;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Logger {

    static String logFile = "logs/latest.log";
    static String configFile = "config.yml";
    static String generatorsFile = "generators.yml";
    static String recipesFile = "recipes.yml";
    static String upgradesFile = "upgrades.yml";
    static String limitsFile = "limits.yml";

    private static void log(Object object, LogType logType) {
        String message = logType.getHeader();
        if (object instanceof String string) {
            message += string;
            if (logType != LogType.DEBUG)
                if (logType == LogType.ERROR || logType == LogType.WARNING)
                    Main.getInstance().getLogger().warning(message);
                else Main.getInstance().getLogger().info(message);
        } else if (object instanceof Exception exception) {
            message += exceptionStacktraceToString(exception);
            Main.getInstance().getLogger().log(Level.SEVERE, logType.getHeader(), exception);
        } else {
            message += object.toString();
            if (logType != LogType.DEBUG)
                if (logType == LogType.ERROR || logType == LogType.WARNING)
                    Main.getInstance().getLogger().warning(message);
                else Main.getInstance().getLogger().info(message);
        }
        logToFile(message, logFile);
    }

    public static void error(Object object) {
        log(object, LogType.ERROR);
    }

    public static void info(Object object) {
        log(object, LogType.INFO);
    }

    public static void warn(Object object) {
        if (Main.getSettings().isWarnings()) log(object, LogType.WARNING);
    }

    public static void debugPlayer(Object object) {
        if (Main.getSettings().isPlayersDebug()) log(object, LogType.DEBUG);
    }

    public static void debugPluginLoad(Object object) {
        if (Main.getSettings().isPluginLoadDebug()) log(object, LogType.DEBUG);
    }

    public static void debugPlacedGeneratorsManager(Object object) {
        if (Main.getSettings().isPlacedGeneratorsManagerDebug()) log(object, LogType.DEBUG);
    }

    public static void debugSchedulesManager(Object object) {
        if (Main.getSettings().isSchedulesManagerDebug()) log(object, LogType.DEBUG);
    }

    public static void debug(Object object) {
        log(object, LogType.DEBUG);
    }

    public static void textToConsole(String message) {
        Main.getInstance().getLogger().info(message);
    }

    private static String exceptionStacktraceToString(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        ps.close();
        return baos.toString();
    }

    public static void logToFile(String message, String file) {
        try {
            File dataFolder = Main.getInstance().getDataFolder();
            if (!dataFolder.exists() && !dataFolder.mkdir()) {
                Main.getInstance().getLogger().warning("Cannot create the plugin data folder " + dataFolder);
            }

            File saveTo = new File(Main.getInstance().getDataFolder(), file);
            // FileWriter creates the file if it does not exist yet
            FileWriter fw = new FileWriter(saveTo, StandardCharsets.UTF_8, true);

            PrintWriter pw = new PrintWriter(fw);

            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss] ");

            pw.println(format.format(now) + message);

            pw.flush();

            pw.close();

        } catch (IOException e) {
            // Not Logger.error: that would write to the log file again
            Main.getInstance().getLogger().log(Level.SEVERE, "Cannot write to " + file, e);
        }
    }

    public static void setup() {
        FilesUtils.mkdir("logs");
        File saveTo = new File(Main.getInstance().getDataFolder(), logFile);
        if (saveTo.exists()) {
            try {
                Date now = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
                Files.move(
                        saveTo.toPath(),
                        new File(Main.getInstance().getDataFolder(), "logs/" + format.format(now) + ".log").toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Logger.error("Cannot create new logs file");
                Logger.error(e);
            }
        }
    }

    public static void debugPaste(CommandSender sender) {
        Sched.async(() -> {
            StringBuilder fileString = new StringBuilder();

            for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
                if (e.getKey().toString().contains("java.version"))
                    fileString.append("Java version: ").append(e.getValue()).append("\n");
                else if (e.getKey().toString().contains("user.country"))
                    fileString.append("User country: ").append(e.getValue()).append("\n");
                else if (e.getKey().toString().contains("os.name"))
                    fileString.append("System: ").append(e.getValue()).append("\n");
            }

            fileString.append("\n");

            fileString
                    .append("Server version: ")
                    .append(Main.getInstance().getServer().getVersion())
                    .append("\n");
            fileString
                    .append("Plugin version: ")
                    .append(Main.getInstance().getPluginMeta().getVersion())
                    .append("\n");
            fileString
                    .append("Enabled dependencies: ")
                    .append(Main.getDependencies().getDependencies())
                    .append("\n");
            fileString.append("Registered addons: ").append(Addons.getAddons()).append("\n");

            fileString.append("Enabled plugins: ");
            for (Plugin plugin :
                    Main.getInstance().getServer().getPluginManager().getPlugins())
                fileString
                        .append(plugin.getName())
                        .append(" ")
                        .append(plugin.getPluginMeta().getVersion())
                        .append(", ");

            fileString.append("\n\n");

            fileString
                    .append("Loaded generators: ")
                    .append(Main.getPlacedGenerators().getLoadedGeneratorsAmount())
                    .append("/")
                    .append(Main.getDatabases().getDb().getGeneratorsAmount())
                    .append("\n");
            fileString
                    .append("Scheduled generators: ")
                    .append(Main.getSchedules().getAmount())
                    .append("\n");

            fileString.append("\n\n");

            try {
                fileString.append(getLinesFromFile(logFile)).append("\n");
                fileString.append(getLinesFromFile(configFile)).append("\n");
                fileString.append(getLinesFromFile(generatorsFile)).append("\n");
                fileString.append(getLinesFromFile(upgradesFile)).append("\n");
                fileString.append(getLinesFromFile(limitsFile)).append("\n");
                fileString.append(getLinesFromFile(recipesFile)).append("\n");

                for (Addon a : Addons.getAddons()) {
                    if (a.getConfigs() != null) {
                        for (String f : a.getConfigs()) {
                            fileString.append(getLinesFromFile(f)).append("\n");
                        }
                    }
                }

                String url = postPaste(sender, fileString.toString());
                if (url != null) {
                    Lang.getMessageStorage().send(sender, Message.COMMANDS_DEBUG_DONE, "<url>", url);
                }

            } catch (IOException e) {
                Lang.getMessageStorage().send(sender, Message.COMMANDS_DEBUG_ERROR);
                Logger.error(e);
            }
        });
    }

    private static String getLinesFromFile(String file) throws IOException {
        StringBuilder fileStrings = new StringBuilder("########################################\n########## " + file
                + " ##########\n########################################\n\n");
        java.nio.file.Path path = Paths.get(Main.getInstance().getDataFolder().getPath(), file);
        if (!Files.exists(path))
            return fileStrings.append("(file doesn't exist)\n\n").toString();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String l : lines) {
            if (!l.contains("password")) fileStrings.append(l).append("\n");
        }
        return fileStrings.toString();
    }

    /**
     * Uploads text to mclo.gs, a paste service for Minecraft server logs that needs no API key.
     * Pastes are kept for 90 days; at most 10 MiB and 25000 lines.
     *
     * @return the paste's URL, or null if the upload failed
     */
    private static String postPaste(CommandSender sender, String text) {
        try {
            byte[] postData =
                    ("content=" + URLEncoder.encode(text, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);

            HttpsURLConnection conn = (HttpsURLConnection) new URL("https://api.mclo.gs/1/log").openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "KGenerators Regenerated");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getResponseCode() < 400 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
            }

            // {"success":true,"id":"...","url":"https:\/\/mclo.gs\/...",...}
            Matcher url = Pattern.compile("\"url\"\\s*:\\s*\"([^\"]+)\"").matcher(response);
            if (response.indexOf("\"success\":true") >= 0 && url.find())
                return url.group(1).replace("\\/", "/");

            Logger.error("Logger: Uploading debug to mclo.gs failed: " + response);
        } catch (IOException e) {
            Logger.error(e);
        }
        Lang.getMessageStorage().send(sender, Message.COMMANDS_DEBUG_ERROR);
        return null;
    }
}
