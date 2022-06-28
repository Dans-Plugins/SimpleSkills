package dansplugins.simpleskills.utils;

import dansplugins.simpleskills.SimpleSkills;

import java.util.logging.Level;

/**
 * @author Daniel Stephenson
 */
public class Logger {
    private final SimpleSkills simpleSkills;

    public Logger(SimpleSkills simpleSkills) {
        this.simpleSkills = simpleSkills;
    }

    public void log(String message) {
        if (simpleSkills.isDebugEnabled()) {
            simpleSkills.getLogger().log(Level.INFO, message);
        }
    }

}
