package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.AbstractSkill;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class SkillCommand extends AbstractPluginCommand {

    public SkillCommand() {
        super(
                new ArrayList<>(Collections.singletonList("skill")),
                new ArrayList<>(Collections.singletonList("ss.skill"))
        );
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("SkillHUsage")));
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String skillName = args[0];
        AbstractSkill skill = PersistentData.getInstance().getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(LocalMessageService.getInstance()
                    .convert(LocalMessageService.getInstance().getlang().getString("SkillNotFound")));
            return false;
        }
        skill.sendInfo(commandSender);
        return true;
    }

}