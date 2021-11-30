package dansplugins.simpleskills.utils;

import dansplugins.simpleskills.SimpleSkills;

public class Logger {

    private static Logger instance;

    private Logger() {

    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        if (SimpleSkills.getInstance().isDebugEnabled()) {
            System.out.println("[SimpleSkills] " + message);
        }
    }

}
