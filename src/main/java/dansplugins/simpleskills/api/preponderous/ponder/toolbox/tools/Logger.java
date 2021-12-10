/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.toolbox.tools;

import preponderous.ponder.Ponder;

public class Logger {
    private Ponder ponder;

    public Logger(Ponder ponder) {
        this.ponder = ponder;
    }

    public boolean log(boolean debug, String message) {
        if (this.ponder.getPlugin() == null) {
            System.out.println("Error: Plugin was null.");
            return false;
        }
        if (debug) {
            System.out.println("[" + this.ponder.getPlugin().getName() + "] " + message);
            return true;
        }
        return false;
    }

    public void print(String message) {
        if (this.ponder.getPlugin() == null) {
            System.out.println("Error: Plugin was null.");
            return;
        }
        System.out.println("[" + this.ponder.getPlugin().getName() + "] " + message);
    }
}

