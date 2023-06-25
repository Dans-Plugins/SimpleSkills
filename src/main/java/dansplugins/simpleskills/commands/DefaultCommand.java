package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.message.MessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class DefaultCommand extends AbstractPluginCommand {
    private final MessageService messageService;
    private final SimpleSkills simpleSkills;

    public DefaultCommand(MessageService messageService, SimpleSkills simpleSkills) {
        super(
                new ArrayList<>(Collections.singletonList("default")),
                new ArrayList<>(Collections.singletonList("ss.default"))
        );
        this.messageService = messageService;
        this.simpleSkills = simpleSkills;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        for (String Dfc : messageService.getlang().getStringList("Default-Command"))
            commandSender.sendMessage(messageService.convert(Dfc).replaceAll("%version%", simpleSkills.getVersion()).replaceAll("%author%", simpleSkills.getDescription().getAuthors().toString()));
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}