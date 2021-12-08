package dansplugins.simpleskills.config;

import preponderous.ponder.misc.ConfigurationFile;

public class PluginConfig {
    private static PluginConfig instance;

    ConfigurationFile configFile;

    private PluginConfig() {
        configFile = new ConfigurationFile("config", true);
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