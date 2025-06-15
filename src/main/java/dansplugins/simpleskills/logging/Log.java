package dansplugins.simpleskills.logging;

import dansplugins.simpleskills.SimpleSkills;

/**
 * @author Daniel Stephenson
 */
public class Log {
    private final SimpleSkills simpleSkills;

    public Log(SimpleSkills simpleSkills) {
        this.simpleSkills = simpleSkills;
    }

    public void info(String message) {
        if (simpleSkills.isDebugEnabled()) {
            getPluginLoggerFromSpigot().info(message);
        }
    }

    public void warning(String message) {
        if (simpleSkills.isDebugEnabled()) {
            getPluginLoggerFromSpigot().warning(message);
        }
    }

    public void error(String message) {
        if (simpleSkills.isDebugEnabled()) {
            getPluginLoggerFromSpigot().severe(message);
        }
    }

    private java.util.logging.Logger getPluginLoggerFromSpigot() {
        return simpleSkills.getLogger();
    }

}
