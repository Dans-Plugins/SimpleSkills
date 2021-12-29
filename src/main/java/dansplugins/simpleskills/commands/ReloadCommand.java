package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.services.LocalConfigService;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class ReloadCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("reload"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.reload"));

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
        LocalMessageService.getInstance().reloadlang();
        LocalConfigService.getInstance().reloadconfig();
        commandSender.sendMessage(ChatColor.GREEN + "Reloaded");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
