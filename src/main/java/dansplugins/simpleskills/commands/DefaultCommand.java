package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class DefaultCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("default"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.default"));

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
        for (String Dfc : LocalMessageService.getInstance().getlang().getStringList("Default-Command"))
            commandSender.sendMessage(LocalMessageService.getInstance().convert(Dfc).replaceAll("%version%", SimpleSkills.getInstance().getVersion()).replaceAll("%author%", SimpleSkills.getInstance().getDescription().getAuthors().toString()));
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}