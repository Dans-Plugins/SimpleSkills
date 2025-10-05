package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import dansplugins.simpleskills.utils.UUIDValidator;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TopCommand extends AbstractPluginCommand {
    private final PlayerRecordRepository playerRecordRepository;
    private final MessageService messageService;
    private final SkillRepository skillRepository;

    public TopCommand(PlayerRecordRepository playerRecordRepository, MessageService messageService, SkillRepository skillRepository) {
        super(
                new ArrayList<>(Collections.singletonList("top")),
                new ArrayList<>(Collections.singletonList("ss.top"))
        );
        this.playerRecordRepository = playerRecordRepository;
        this.messageService = messageService;
        this.skillRepository = skillRepository;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        final PlayerRecordRepository instance = playerRecordRepository;
        final List<PlayerRecord> topPlayers = instance.getTopPlayers();
        for (String message : messageService.getlang().getStringList("Top-Header")) {
            commandSender.sendMessage(messageService.convert(message
                    .replaceAll("%skill%", "All Skills")
            ));
        }
        if (topPlayers == null) return false;
        for (PlayerRecord topPlayer : topPlayers) {
            String playerName = UUIDValidator.getPlayerNameFromUUID(topPlayer.getPlayerUUID());
            for (String message : messageService.getlang().getStringList("Top-Body")) {
                commandSender.sendMessage(messageService.convert(message
                                .replaceAll("%player%", playerName)
                                .replaceAll("%top%", String.valueOf(topPlayer.getOverallSkillLevel())))
                        .replaceAll("%rank%", String.valueOf(topPlayers.indexOf(topPlayer) + 1))
                );
            }
        }
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String skillName = String.join(" ", args);
        AbstractSkill skill = skillRepository.getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(messageService
                    .convert(Objects.requireNonNull(messageService.getlang()
                            .getString("SkillNotFound"))));
            return false;
        }
        final List<PlayerRecord> topPlayerRecords = playerRecordRepository.getTopPlayerRecords(skill.getId());
        if (topPlayerRecords == null) {
            commandSender.sendMessage(messageService
                    .convert(messageService.getlang().getString("NoTop")
                            .replaceAll("%skill%", skill.getName())));

            return false;
        }
        for (String message : messageService.getlang().getStringList("Top-Header")) {
            commandSender.sendMessage(messageService.convert(message
                    .replaceAll("%skill%", skill.getName())
            ));
        }
        for (PlayerRecord playerRecord : topPlayerRecords) {
            String playerName = UUIDValidator.getPlayerNameFromUUID(playerRecord.getPlayerUUID());
            for (String message : messageService.getlang().getStringList("Top-Body")) {
                commandSender.sendMessage(messageService.convert(message
                        .replaceAll("%player%", playerName)
                        .replaceAll("%top%", String.valueOf(playerRecord.getSkillLevel(skill.getId(), false)))
                        .replaceAll("%rank%", String.valueOf(topPlayerRecords.indexOf(playerRecord) + 1))
                ));
            }
        }
        return true;
    }
}