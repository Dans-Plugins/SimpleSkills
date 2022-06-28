package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.services.MessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class StatsCommand extends AbstractPluginCommand {
    private final MessageService messageService;
    private final PersistentData persistentData;

    public StatsCommand(MessageService messageService, PersistentData persistentData) {
        super(
                new ArrayList<>(Collections.singletonList("stats")),
                new ArrayList<>(Collections.singletonList("ss.stats"))
        );
        this.messageService = messageService;
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender commandSender) {

        for (String sc : messageService.getlang().getStringList("Stats"))
            commandSender.sendMessage(messageService.convert(sc)
                    .replaceAll("%nos%", String.valueOf(persistentData.getSkills().size()))
                    .replaceAll("%nopr%", String.valueOf(persistentData.getPlayerRecords().size()))
                    .replaceAll("%uns%", String.valueOf(persistentData.getNumUnknownSkills())));
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
