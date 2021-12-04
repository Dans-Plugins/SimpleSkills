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
        commandSender.sendMessage(ChatColor.RED + "Usage: /ss top \"skill name\"");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        ArrayList<String> doubleQuoteArgs = SimpleSkills.getInstance().getToolbox().getArgumentParser().getArgumentsInsideDoubleQuotes(args);
        if (doubleQuoteArgs.size() == 0) {
            commandSender.sendMessage(ChatColor.RED + "Skill name must be designated between double quotes.");
            return false;
        }
        String skillName = doubleQuoteArgs.get(0);
        Skill skill = PersistentData.getInstance().getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(ChatColor.RED + "That skill wasn't found.");
            return false;
        }
        PlayerRecord topPlayerRecord = PersistentData.getInstance().getTopPlayerRecord(skill.getID());
        String playerName = SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(topPlayerRecord.getPlayerUUID());
        commandSender.sendMessage(ChatColor.RED + "The player with the highest leve' in " + skill.getName() + " is " + playerName + " at level " + topPlayerRecord.getSkillLevel(skill.getID()));
        return true;
    }
}