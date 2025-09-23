package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Daniel Stephenson
 */
public class StatsCommand extends AbstractPluginCommand {
    private final MessageService messageService;
    private final PlayerRecordRepository playerRecordRepository;
    private final SkillRepository skillRepository;

    public StatsCommand(MessageService messageService, PlayerRecordRepository playerRecordRepository, SkillRepository skillRepository) {
        super(
                new ArrayList<>(Collections.singletonList("stats")),
                new ArrayList<>(Collections.singletonList("ss.stats"))
        );
        this.messageService = messageService;
        this.playerRecordRepository = playerRecordRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public boolean execute(CommandSender commandSender) {

        for (String sc : messageService.getlang().getStringList("Stats"))
            commandSender.sendMessage(messageService.convert(sc)
                    .replaceAll("%nos%", String.valueOf(skillRepository.getActiveSkills().size()))
                    .replaceAll("%nopr%", String.valueOf(playerRecordRepository.getPlayerRecords().size()))
                    .replaceAll("%uns%", String.valueOf(getNumUnknownSkills())));
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }

    public int getNumUnknownSkills() {
        return skillRepository.getActiveSkills().size() - getNumKnownSkills();
    }

    public int getNumKnownSkills() {
        int knownSkills = 0;
        for (AbstractSkill skill : skillRepository.getActiveSkills()) {
            for (PlayerRecord record : playerRecordRepository.getPlayerRecords()) {
                if (record.isKnown(skill)) {
                    knownSkills++;
                    break;
                }
            }
        }
        return knownSkills;
    }
}
