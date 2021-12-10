/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.toolbox;

import preponderous.ponder.Ponder;
import preponderous.ponder.toolbox.tools.ArgumentParser;
import preponderous.ponder.toolbox.tools.BlockChecker;
import preponderous.ponder.toolbox.tools.EventHandlerRegistry;
import preponderous.ponder.toolbox.tools.Logger;
import preponderous.ponder.toolbox.tools.Messenger;
import preponderous.ponder.toolbox.tools.PermissionChecker;
import preponderous.ponder.toolbox.tools.UUIDChecker;

public class Toolbox {
    private ArgumentParser argumentParser = new ArgumentParser();
    private BlockChecker blockChecker = new BlockChecker();
    private EventHandlerRegistry eventHandlerRegistry;
    private Logger logger;
    private Messenger messenger;
    private PermissionChecker permissionChecker;
    private UUIDChecker uuidChecker;

    public Toolbox(Ponder ponder) {
        this.eventHandlerRegistry = new EventHandlerRegistry(ponder);
        this.logger = new Logger(ponder);
        this.messenger = new Messenger();
        this.permissionChecker = new PermissionChecker();
        this.uuidChecker = new UUIDChecker();
    }

    public ArgumentParser getArgumentParser() {
        return this.argumentParser;
    }

    public BlockChecker getBlockChecker() {
        return this.blockChecker;
    }


    public EventHandlerRegistry getEventHandlerRegistry() {
        return this.eventHandlerRegistry;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Messenger getMessenger() {
        return this.messenger;
    }

    public PermissionChecker getPermissionChecker() {
        return this.permissionChecker;
    }

    public UUIDChecker getUUIDChecker() {
        return this.uuidChecker;
    }
}

