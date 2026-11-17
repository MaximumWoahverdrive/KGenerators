package me.kryniowesegryderiusz.kgenerators.utils.immutable;

import java.io.File;
import java.io.IOException;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config extends YamlConfiguration {
    @Getter
    File file;

    public Config(File file) {
        super();
        this.file = file;
    }

    public void loadConfig() throws IOException, InvalidConfigurationException {
        load(file);
    }

    public void saveConfig() throws IOException {
        save(file);
    }
}
