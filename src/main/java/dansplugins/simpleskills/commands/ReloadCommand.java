package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

public class ReloadCommand extends AbstractPluginCommand {
    private final MessageService messageService;
    private final ConfigService configService;

    public ReloadCommand(MessageService messageService, ConfigService configService) {
        super(
                new ArrayList<>(Collections.singletonList("reload")),
                new ArrayList<>(Collections.singletonList("ss.reload"))
        );
        this.messageService = messageService;
        this.configService = configService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        messageService.reloadlang();
        configService.reloadconfig();
        commandSender.sendMessage(ChatColor.GREEN + "Reloaded");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }

}
