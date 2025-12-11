package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Command to toggle skill benefits on/off for a player
 * @author Daniel Stephenson
 */
public class ToggleCommand extends AbstractPluginCommand {
    private final MessageService messageService;
    private final PlayerRecordRepository playerRecordRepository;
    private final SkillRepository skillRepository;

    public ToggleCommand(MessageService messageService, PlayerRecordRepository playerRecordRepository, SkillRepository skillRepository) {
        super(
                new ArrayList<>(Collections.singletonList("toggle")),
                new ArrayList<>(Collections.singletonList("ss.toggle"))
        );
        this.messageService = messageService;
        this.playerRecordRepository = playerRecordRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(messageService.convert(messageService.getlang().getString("Toggle-Usage")));
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(messageService.convert(messageService.getlang().getString("MustBePlayer")));
            return false;
        }

        if (args.length < 1) {
            return execute(commandSender);
        }

        Player player = (Player) commandSender;
        String skillName = args[0];

        AbstractSkill skill = skillRepository.getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(messageService.convert(messageService.getlang().getString("SkillNotFound")));
            return false;
        }

        PlayerRecord record = playerRecordRepository.getPlayerRecord(player.getUniqueId());
        if (record == null) {
            commandSender.sendMessage(messageService.convert(messageService.getlang().getString("DontHaveRecord")));
            return false;
        }

        // Check if player has learned the skill
        if (!record.isKnown(skill)) {
            commandSender.sendMessage(messageService.convert(messageService.getlang().getString("SkillNotLearned")));
            return false;
        }

        record.toggleBenefit(skill.getId());
        boolean enabled = record.isBenefitEnabled(skill.getId());

        String message = enabled ? 
            messageService.getlang().getString("Toggle-Enabled") : 
            messageService.getlang().getString("Toggle-Disabled");
        
        commandSender.sendMessage(messageService.convert(message.replaceAll("%skill%", skill.getName())));
        return true;
    }
}
