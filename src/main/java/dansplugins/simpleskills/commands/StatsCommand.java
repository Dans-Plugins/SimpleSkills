package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class StatsCommand extends AbstractPluginCommand {

    public StatsCommand() {
        super(
                new ArrayList<>(Collections.singletonList("stats")),
                new ArrayList<>(Collections.singletonList("ss.stats"))
        );
    }

    @Override
    public boolean execute(CommandSender commandSender) {

        for (String sc : LocalMessageService.getInstance().getlang().getStringList("Stats"))
            commandSender.sendMessage(LocalMessageService.getInstance().convert(sc)
                    .replaceAll("%nos%", String.valueOf(PersistentData.getInstance().getSkills().size()))
                    .replaceAll("%nopr%", String.valueOf(PersistentData.getInstance().getPlayerRecords().size()))
                    .replaceAll("%uns%", String.valueOf(PersistentData.getInstance().getNumUnknownSkills())));
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
