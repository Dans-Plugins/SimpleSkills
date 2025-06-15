package dansplugins.simpleskills.logging;

import dansplugins.simpleskills.SimpleSkills;

import java.util.logging.Level;

import static java.util.logging.Level.INFO;

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
            getLogger().info(message);
        }
    }

    public java.util.logging.Logger getLogger() {
        return simpleSkills.getLogger();
    }

}
