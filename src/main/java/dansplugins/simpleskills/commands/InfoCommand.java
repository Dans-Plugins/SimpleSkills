package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class InfoCommand extends AbstractCommand {
    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("info"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.info"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
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
        UUID playerUUID = SimpleSkills.getInstance().getToolbox().getUUIDChecker().findUUIDBasedOnPlayerName(playerName);
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