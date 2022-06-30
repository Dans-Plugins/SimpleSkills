package dansplugins.simpleskills.services;

/*
    To add a new config option, the following methods must be altered:
    - saveMissingConfigDefaultsIfNotPresent
    - setConfigOption()
    - sendConfigList()
 */

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Daniel Stephenson
 */
public class ConfigService {
    private final SimpleSkills simpleSkills;

    private final boolean altered = false;
    private File configFile;
    private FileConfiguration config;

    public ConfigService(SimpleSkills simpleSkills) {
        this.simpleSkills = simpleSkills;
    }

    public void createconfig() {
        configFile = new File(simpleSkills.getDataFolder(), "config.yml");

        if (!configFile.exists()) simpleSkills.saveResource("config.yml", false);
        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getconfig() {
        return config;
    }


    public void reloadconfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveconfig() {
        try {
            config.save(configFile);
        } catch (IOException ignored) {
        }
    }

}