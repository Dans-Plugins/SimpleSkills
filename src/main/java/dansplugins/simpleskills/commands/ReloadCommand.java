package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.services.LocalConfigService;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends AbstractPluginCommand {

    public ReloadCommand() {
        super(new ArrayList<>(List.of("reload")), new ArrayList<>(List.of("ss.reload")));
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
