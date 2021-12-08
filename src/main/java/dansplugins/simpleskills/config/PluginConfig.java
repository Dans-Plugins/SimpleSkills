package dansplugins.simpleskills.config;

import dansplugins.simpleskills.SimpleSkills;
import preponderous.ponder.misc.ConfigurationFile;

public class PluginConfig {
    private static PluginConfig instance;

    ConfigurationFile configFile;

    private PluginConfig() {
        configFile = new ConfigurationFile("config", true, SimpleSkills.getInstance());
    }

    public static PluginConfig getInstance() {
        if (instance == null) {
            instance = new PluginConfig();
        }
        return instance;
    }

    public ConfigurationFile getConfig() {
        return configFile;
    }

}