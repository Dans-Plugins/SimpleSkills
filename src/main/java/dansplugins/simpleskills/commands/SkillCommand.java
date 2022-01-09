package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.abs.Skill;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Daniel Stephenson
 */
public class SkillCommand extends AbstractPluginCommand {

    public SkillCommand() {
        super(new ArrayList<>(Arrays.asList("skill")), new ArrayList<>(Arrays.asList("ss.skill")));
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