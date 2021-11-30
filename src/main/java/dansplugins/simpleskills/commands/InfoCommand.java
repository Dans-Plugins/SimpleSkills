package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

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
            player.sendMessage(ChatColor.RED + "You don't have a player record.");
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
            commandSender.sendMessage(ChatColor.RED + "That player wasn't found.");
            return false;
        }
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(playerUUID);
        if (playerRecord == null) {
            commandSender.sendMessage(ChatColor.RED + "That player doesn't have a player record.");
            return false;
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }
}