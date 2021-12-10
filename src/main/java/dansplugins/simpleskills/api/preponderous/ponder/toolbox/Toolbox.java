package dansplugins.simpleskills.api.preponderous.ponder.toolbox;

import dansplugins.simpleskills.api.preponderous.ponder.Ponder;
import dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools.*;
import dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools.*;

/**
 * @author Daniel Stephenson
 */
public class Toolbox {

    private ArgumentParser argumentParser;
    private BlockChecker blockChecker;
    private ColorChecker colorChecker;
    private ColorConverter colorConverter;
    private EventHandlerRegistry eventHandlerRegistry;
    private Logger logger;
    private Messenger messenger;
    private PermissionChecker permissionChecker;
    private UUIDChecker uuidChecker;

    /**
     * Constructor to initialize the tools of the toolbox.
     *
     */
    public Toolbox(Ponder ponder) {
        argumentParser = new ArgumentParser();
        blockChecker = new BlockChecker();
        colorChecker = new ColorChecker();
        colorConverter = new ColorConverter();
        eventHandlerRegistry = new EventHandlerRegistry(ponder);
        logger = new Logger(ponder);
        messenger = new Messenger();
        permissionChecker = new PermissionChecker();
        uuidChecker = new UUIDChecker();
    }

    /**
     * Method to get the argument parser tool.
     *
     * @return {@link ArgumentParser}
     */
    public ArgumentParser getArgumentParser() {
        return argumentParser;
    }

    /**
     * Method to drop the get the block checker.
     *
     * @return {@link BlockChecker}
     */
    public BlockChecker getBlockChecker() {
        return blockChecker;
    }

    /**
     * Method to get the color checker tool.
     *
     * @return {@link ColorChecker}
     */
    public ColorChecker getColorChecker() {
        return colorChecker;
    }

    /**
     * Method to drop the get the color converter.
     *
     * @return {@link EventHandlerRegistry}
     */
    public ColorConverter getColorConverter() {
        return colorConverter;
    }

    /**
     * Method to get the event handler registry tool.
     *
     * @return {@link EventHandlerRegistry}
     */
    public EventHandlerRegistry getEventHandlerRegistry() {
        return eventHandlerRegistry;
    }

    /**
     * Method to get the logger tool.
     *
     * @return {@link Logger}
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Method to get the messenger tool.
     *
     * @return {@link Messenger}
     */
    public Messenger getMessenger() {
        return messenger;
    }

    /**
     * Method to get the permission checker tool.
     *
     * @return {@link PermissionChecker}
     */
    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    /**
     * Method to get the UUID Checker tool.
     *
     * @return {@link UUIDChecker}
     */
    public UUIDChecker getUUIDChecker() {
        return uuidChecker;
    }
}
