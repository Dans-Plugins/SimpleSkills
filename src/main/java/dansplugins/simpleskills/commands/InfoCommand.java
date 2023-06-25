package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.message.MessageService;
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
    private final PlayerRecordRepository playerRecordRepository;
    private final MessageService messageService;

    public InfoCommand(PlayerRecordRepository playerRecordRepository, MessageService messageService) {
        super(
                new ArrayList<>(Collections.singletonList("info")),
                new ArrayList<>(Collections.singletonList("ss.info"))
        );
        this.playerRecordRepository = playerRecordRepository;
        this.messageService = messageService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;
        PlayerRecord playerRecord = playerRecordRepository.getPlayerRecord(player.getUniqueId());
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
        PlayerRecord playerRecord = playerRecordRepository.getPlayerRecord(playerUUID);
        if (playerRecord == null) {
            commandSender.sendMessage(messageService.convert(messageService.getlang().getString("DoesntHaveRecord")));
            return false;
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }
}