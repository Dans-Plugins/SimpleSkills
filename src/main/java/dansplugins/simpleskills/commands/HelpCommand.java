package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class HelpCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("help"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.help"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
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
