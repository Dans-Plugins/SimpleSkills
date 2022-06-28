package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.services.MessageService;
import dansplugins.simpleskills.skills.abs.AbstractSkill;

import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class SkillCommand extends AbstractPluginCommand {
    private final MessageService messageService;
    private final PersistentData persistentData;

    public SkillCommand(MessageService messageService, PersistentData persistentData) {
        super(
                new ArrayList<>(Collections.singletonList("skill")),
                new ArrayList<>(Collections.singletonList("ss.skill"))
        );
        this.messageService = messageService;
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(messageService.convert(messageService.getlang().getString("SkillHUsage")));
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String skillName = args[0];
        AbstractSkill skill = persistentData.getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(messageService
                    .convert(messageService.getlang().getString("SkillNotFound")));
            return false;
        }
        skill.sendInfo(commandSender);
        return true;
    }

}