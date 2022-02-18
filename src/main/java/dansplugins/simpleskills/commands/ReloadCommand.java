package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.services.LocalConfigService;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

public class ReloadCommand extends AbstractPluginCommand {

    public ReloadCommand() {
        super(
                new ArrayList<>(Collections.singletonList("reload")),
                new ArrayList<>(Collections.singletonList("ss.reload"))
        );
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
