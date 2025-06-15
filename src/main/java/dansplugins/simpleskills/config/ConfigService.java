package dansplugins.simpleskills.config;

/*
    To add a new config option, the following methods must be altered:
    - saveMissingConfigDefaultsIfNotPresent
    - setConfigOption()
    - sendConfigList()
 */

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.logging.Log;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author Daniel Stephenson
 */
public class ConfigService {
    private final SimpleSkills simpleSkills;

    private final boolean altered = false;
    private File configFile;
    private FileConfiguration config;
    private final Log log;

    public ConfigService(SimpleSkills simpleSkills, Log log) {
        this.simpleSkills = simpleSkills;
        this.log = log;
    }

    public void createConfig() {
        log.info("Creating config.yml file...");
        configFile = new File(simpleSkills.getDataFolder(), "config.yml");

        if (!configFile.exists()) simpleSkills.saveResource("config.yml", false);
        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            log.error("Failed to load config.yml file.");
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException ignored) {
        }
    }

}