package dansplugins.simpleskills.config;

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

    public void createConfig() {
        simpleSkills.getLogger().info("Creating config.yml file..."); // note: must use simpleSkills.getLogger() to avoid circular dependency issues
        configFile = new File(simpleSkills.getDataFolder(), "config.yml");

        if (!configFile.exists()) simpleSkills.saveResource("config.yml", false);
        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            simpleSkills.getLogger().severe("Failed to load config.yml file."); // note: must use simpleSkills.getLogger() to avoid circular dependency issues
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