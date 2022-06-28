package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.objects.PlayerRecord;
import dansplugins.simpleskills.services.MessageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class InfoCommand extends AbstractPluginCommand {
    private final PersistentData persistentData;
    private final MessageService messageService;

    public InfoCommand(PersistentData persistentData, MessageService messageService) {
        super(
                new ArrayList<>(Collections.singletonList("info")),
                new ArrayList<>(Collections.singletonList("ss.info"))
        );
        this.persistentData = persistentData;
        this.messageService = messageService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;
        PlayerRecord playerRecord = persistentData.getPlayerRecord(player.getUniqueId());
        if (playerRecord == null) {
            player.sendMessage(messageService.convert(messageService.getlang().getString("DontHaveRecord")));
            return false;
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String playerName = args[0];
        UUID playerUUID = new UUIDChecker().findUUIDBasedOnPlayerName(playerName);
        if (playerUUID == null) {
            commandSender.sendMessage(messageService.convert(messageService.getlang().getString("NotFound")));
            return false;
        }
        PlayerRecord playerRecord = persistentData.getPlayerRecord(playerUUID);
        if (playerRecord == null) {
            commandSender.sendMessage(messageService.convert(messageService.getlang().getString("DoesntHaveRecord")));
            return false;
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }
}