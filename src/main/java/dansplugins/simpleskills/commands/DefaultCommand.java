package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class DefaultCommand extends AbstractPluginCommand {

    public DefaultCommand() {
        super(
                new ArrayList<>(Collections.singletonList("default")),
                new ArrayList<>(Collections.singletonList("ss.default"))
        );
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