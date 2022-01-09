package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {

    public HelpCommand() {
        super(new ArrayList<>(Arrays.asList("help")), new ArrayList<>(Arrays.asList("ss.help")));
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
