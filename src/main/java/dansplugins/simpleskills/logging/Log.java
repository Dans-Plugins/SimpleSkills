package dansplugins.simpleskills.logging;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.config.ConfigService;

/**
 * @author Daniel Stephenson
 */
public class Log {
    private final SimpleSkills simpleSkills;
    private final ConfigService configService;

    public Log(SimpleSkills simpleSkills, ConfigService configService) {
        this.simpleSkills = simpleSkills;
        this.configService = configService;

    }

    public void debug(String message) {
        if (isDebugEnabled()) {
            getPluginLoggerFromSpigot().info("[DEBUG] " + message);
        }
    }

    public void info(String message) {
        getPluginLoggerFromSpigot().info(message);
    }

    public void warning(String message) {
        getPluginLoggerFromSpigot().warning(message);
    }

    public void error(String message) {
        getPluginLoggerFromSpigot().severe(message);
    }

    private java.util.logging.Logger getPluginLoggerFromSpigot() {
        return simpleSkills.getLogger();
    }

    private boolean isDebugEnabled() {
        return configService.getConfig().getBoolean("debugMode");
    }

}
