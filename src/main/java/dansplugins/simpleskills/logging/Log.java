package dansplugins.simpleskills.logging;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.config.ConfigService;

/**
 * @author Daniel Stephenson
 */
public class Log {
    private final SimpleSkills simpleSkills;
    private final ConfigService configService;

    private final static String DEBUG_PREFIX = "[DEBUG] ";
    private final static String INFO_PREFIX = "[INFO] ";
    private final static String WARNING_PREFIX = "[WARNING] ";
    private final static String ERROR_PREFIX = "[ERROR] ";

    public Log(SimpleSkills simpleSkills, ConfigService configService) {
        this.simpleSkills = simpleSkills;
        this.configService = configService;

    }

    public void debug(String message) {
        if (isDebugEnabled()) {
            getPluginLoggerFromSpigot().info(DEBUG_PREFIX + message);
        }
    }

    public void info(String message) {
        getPluginLoggerFromSpigot().info(INFO_PREFIX + message);
    }

    public void warning(String message) {
        getPluginLoggerFromSpigot().warning(WARNING_PREFIX + message);
    }

    public void error(String message) {
        getPluginLoggerFromSpigot().severe(ERROR_PREFIX + message);
    }

    private java.util.logging.Logger getPluginLoggerFromSpigot() {
        return simpleSkills.getLogger();
    }

    private boolean isDebugEnabled() {
        return configService.getConfig().getBoolean("debugMode");
    }

}
