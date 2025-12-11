package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;

import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class SkillCommand extends AbstractPluginCommand {
    private final MessageService messageService;
    private final SkillRepository skillRepository;

    public SkillCommand(MessageService messageService, SkillRepository skillRepository) {
        super(
                new ArrayList<>(Collections.singletonList("skill")),
                new ArrayList<>(Collections.singletonList("ss.skill"))
        );
        this.messageService = messageService;
        this.skillRepository = skillRepository;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(messageService.convert(messageService.getlang().getString("SkillHUsage")));
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String skillName = args[0];
        AbstractSkill skill = skillRepository.getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(messageService
                    .convert(messageService.getlang().getString("SkillNotFound")));
            return false;
        }
        skill.sendInfo(commandSender);
        return true;
    }

}