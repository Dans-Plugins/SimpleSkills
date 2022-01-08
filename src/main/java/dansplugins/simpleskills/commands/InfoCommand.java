package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.spigot.tools.UUIDChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class InfoCommand extends AbstractPluginCommand {

    public InfoCommand() {
        super(new ArrayList<>(List.of("info")), new ArrayList<>(List.of("ss.info")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(player.getUniqueId());
        if (playerRecord == null) {
            player.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("DontHaveRecord")));
            return false;
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String playerName = args[0];
        UUIDChecker uuidChecker = new UUIDChecker();
        UUID playerUUID = uuidChecker.findUUIDBasedOnPlayerName(playerName);
        if (playerUUID == null) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("NotFound")));
            return false;
        }
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(playerUUID);
        if (playerRecord == null) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("DoesntHaveRecord")));
            return false;
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }
}