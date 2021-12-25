package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.abs.Skill;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

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
        commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("SkillHUsage")));

        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String skillName = args[0];
        Skill skill = PersistentData.getInstance().getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("SkillNotFound")));

        }
        skill.sendInfo(commandSender);
        return true;
    }
}