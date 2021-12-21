package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.PlayerRecord;
import dansplugins.simpleskills.objects.abs.Skill;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class TopCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("top"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.top"));

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
        commandSender.sendMessage(ChatColor.RED + "Usage: /ss top (skillName)");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String skillName = args[0];
        Skill skill = PersistentData.getInstance().getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(ChatColor.RED + "That skill wasn't found.");
            return false;
        }
        PlayerRecord topPlayerRecord = PersistentData.getInstance().getTopPlayerRecord(skill.getID());
        if (topPlayerRecord == null) {
            commandSender.sendMessage(ChatColor.RED + "No one is very skilled at " + skill.getName() + ".");
            return false;
        }
        String playerName = SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(topPlayerRecord.getPlayerUUID());
        commandSender.sendMessage(ChatColor.AQUA + "Top Player in " + skill.getName() + " -> " + playerName + " - LVL: " + topPlayerRecord.getSkillLevel(skill.getID()));
        return true;
    }
}