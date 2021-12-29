package dansplugins.simpleskills.services;

/*
    To add a new config option, the following methods must be altered:
    - saveMissingConfigDefaultsIfNotPresent
    - setConfigOption()
    - sendConfigList()
 */

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.nms.NMSVersion;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Daniel Stephenson
 */
public class LocalConfigService {

    private static LocalConfigService instance;
    private boolean altered = false;

    String nms = NMSVersion.getNMSVersion();
    String mcver = NMSVersion.formatNMSVersion(nms);

    private LocalConfigService() {

    }


    public static LocalConfigService getInstance() {
        if (instance == null) {
            instance = new LocalConfigService();
        }
        return instance;
    }
    private File configFile;
    private FileConfiguration config;


    public void createconfig() {
        configFile = new File(SimpleSkills.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) SimpleSkills.getInstance().saveResource("config.yml", false);
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