package me.kryniowesegryderiusz.kgenerators.settings.objects;

import com.cryptomorin.xseries.XSound;
import java.util.Optional;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import org.bukkit.entity.Player;

public class Sound {

    XSound xsound;
    float volume = 0.2f;
    float pitch = 0.0f;

    /**
     * Constructor loading sound from config
     *
     * @param cfg config to read from
     * @param path to sound
     */
    public Sound(Config cfg, String path) {
        if (cfg.contains(path + ".sound")) {
            String name = cfg.getString(path + ".sound");
            if (name == null) {
                Logger.error("Config file: " + path + ".sound should be a sound name! It'll not work!");
                return;
            }
            if (!name.equalsIgnoreCase("none")) {
                Optional<XSound> xso = XSound.of(name);
                if (xso.isEmpty()) {
                    Logger.error("Config file: Sound " + name + " doesnt exist! It'll not work!");
                    return;
                }
                this.xsound = xso.get();

                if (cfg.contains(path + ".volume")) this.volume = (float) cfg.getDouble(path + ".volume");
                if (cfg.contains(path + ".pitch")) this.pitch = (float) cfg.getDouble(path + ".pitch");
            }
        }
    }

    public Sound(XSound xsound) {
        this.xsound = xsound;
    }

    public void play(Player p) {
        if (this.xsound != null) this.xsound.play(p, this.volume, this.pitch);
    }
}
