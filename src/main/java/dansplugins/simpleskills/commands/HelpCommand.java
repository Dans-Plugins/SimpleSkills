package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.message.MessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {
    private final MessageService messageService;

    public HelpCommand(MessageService messageService) {
        super(
                new ArrayList<>(Collections.singletonList("help")),
                new ArrayList<>(Collections.singletonList("ss.help"))
        );
        this.messageService = messageService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        for (String Hc : messageService.getlang().getStringList("Help-Command"))
            commandSender.sendMessage(messageService.convert(Hc));
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
