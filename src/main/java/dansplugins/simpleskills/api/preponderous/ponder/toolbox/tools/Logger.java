package dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools;

import dansplugins.simpleskills.api.preponderous.ponder.Ponder;

/**
 * @author Daniel Stephenson
 */
public class Logger {

    private Ponder ponder;

    public Logger(Ponder ponder) {
        this.ponder = ponder;
    }

    /**
     * Method to print a message to the console if the debug flag is set to true.
     *
     * @param debug to check.
     * @param message to print.
     * @return {@link boolean} signifying whether or not the method was successful.
     */
    public boolean log(boolean debug, String message) {
        if (ponder.getPlugin() == null) {
            System.out.println("Error: Plugin was null.");
            return false;
        }
        if (debug) {
            System.out.println("[" + ponder.getPlugin().getName() + "] " + message);
            return true;
        }
        return false;
    }

    /**
     * Method to print a message to the console.
     *
     * @param message to print.
     */
    public void print(String message) {
        if (ponder.getPlugin() == null) {
            System.out.println("Error: Plugin was null.");
            return;
        }
        System.out.println("[" + ponder.getPlugin().getName() + "] " + message);
    }
}
