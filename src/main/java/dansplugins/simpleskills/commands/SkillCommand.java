package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.abs.Skill;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import dansplugins.simpleskills.api.preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class SkillCommand extends AbstractCommand {
    private final ArrayList<String> names = new ArrayList<>(Collections.singletonList("skill"));
    private final ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("ss.skill"));

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
        commandSender.sendMessage(ChatColor.RED + "Usage: /ss skill \"skill name\"");
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
        skill.sendInfo(commandSender);
        return true;
    }
}