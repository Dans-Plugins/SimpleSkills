package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.objects.PlayerRecord;
import dansplugins.simpleskills.services.MessageService;
import dansplugins.simpleskills.skills.abs.AbstractSkill;

import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TopCommand extends AbstractPluginCommand {
    private final PersistentData persistentData;
    private final MessageService messageService;

    public TopCommand(PersistentData persistentData, MessageService messageService) {
        super(
                new ArrayList<>(Collections.singletonList("top")),
                new ArrayList<>(Collections.singletonList("ss.top"))
        );
        this.persistentData = persistentData;
        this.messageService = messageService;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        final PersistentData instance = persistentData;
        final List<PlayerRecord> topPlayers = instance.getTopPlayers();
        for (String message : messageService.getlang().getStringList("Top-Header")) {
            commandSender.sendMessage(messageService.convert(message
                    .replaceAll("%skill%", "All Skills")
            ));
        }
        if (topPlayers == null) return false;
        for (PlayerRecord topPlayer : topPlayers) {
            String playerName = new UUIDChecker()
                    .findPlayerNameBasedOnUUID(topPlayer.getPlayerUUID());
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
        AbstractSkill skill = persistentData.getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(messageService
                    .convert(Objects.requireNonNull(messageService.getlang()
                            .getString("SkillNotFound"))));
            return false;
        }
        final List<PlayerRecord> topPlayerRecords = persistentData.getTopPlayerRecords(skill.getId());
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
            String playerName = new UUIDChecker().findPlayerNameBasedOnUUID(playerRecord.getPlayerUUID());
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