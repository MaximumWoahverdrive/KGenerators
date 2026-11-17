package me.kryniowesegryderiusz.kgenerators.addons.objects;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.addons.Addons;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Create new instance of this class to register new addon in KGenerators
 */
public class Addon {

    @Getter
    private final String name;

    @Getter
    private final String version;

    @Getter
    private final ArrayList<String> configs = new ArrayList<>();

    @Getter
    private final HashMap<String, AddonCommand> commands = new HashMap<>();

    public Addon(Plugin plugin) {
        this.name = plugin.getPluginMeta().getName();
        this.version = plugin.getPluginMeta().getVersion();
        Addons.register(this);
        Logger.info("Addons manager: Registered " + this + " addon");
    }

    public String toString() {
        return this.name + "-" + this.version + "-" + configs + "-" + commands.keySet();
    }

    public interface AddonCommand {
        void onCommand(CommandSender sender, String[] args);
    }
}
