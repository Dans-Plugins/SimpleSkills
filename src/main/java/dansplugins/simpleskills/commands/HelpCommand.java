package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {

    public HelpCommand() {
        super(
                new ArrayList<>(Collections.singletonList("help")),
                new ArrayList<>(Collections.singletonList("ss.help"))
        );
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        for (String Hc : LocalMessageService.getInstance().getlang().getStringList("Help-Command"))
            commandSender.sendMessage(LocalMessageService.getInstance().convert(Hc));
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
