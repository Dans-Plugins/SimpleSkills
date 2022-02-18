package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.AbstractSkill;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TopCommand extends AbstractPluginCommand {

    public TopCommand() {
        super(
                new ArrayList<>(Collections.singletonList("top")),
                new ArrayList<>(Collections.singletonList("ss.top"))
        );
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        final PersistentData instance = PersistentData.getInstance();
        final List<PlayerRecord> topPlayers = instance.getTopPlayers();
        for (String message : LocalMessageService.getInstance().getlang().getStringList("Top-Header")) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(message
                    .replaceAll("%skill%", "All Skills")
            ));
        }
        if (topPlayers == null) return false;
        for (PlayerRecord topPlayer : topPlayers) {
            String playerName = new UUIDChecker()
                    .findPlayerNameBasedOnUUID(topPlayer.getPlayerUUID());
            for (String message : LocalMessageService.getInstance().getlang().getStringList("Top-Body")) {
                commandSender.sendMessage(LocalMessageService.getInstance().convert(message
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
        AbstractSkill skill = PersistentData.getInstance().getSkill(skillName);
        if (skill == null) {
            commandSender.sendMessage(LocalMessageService.getInstance()
                    .convert(Objects.requireNonNull(LocalMessageService.getInstance().getlang()
                            .getString("SkillNotFound"))));
            return false;
        }
        final List<PlayerRecord> topPlayerRecords = PersistentData.getInstance().getTopPlayerRecords(skill.getId());
        if (topPlayerRecords == null) {
            commandSender.sendMessage(LocalMessageService.getInstance()
                    .convert(LocalMessageService.getInstance().getlang().getString("NoTop")
                            .replaceAll("%skill%", skill.getName())));

            return false;
        }
        for (String message : LocalMessageService.getInstance().getlang().getStringList("Top-Header")) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(message
                    .replaceAll("%skill%", skill.getName())
            ));
        }
        for (PlayerRecord playerRecord : topPlayerRecords) {
            String playerName = new UUIDChecker().findPlayerNameBasedOnUUID(playerRecord.getPlayerUUID());
            for (String message : LocalMessageService.getInstance().getlang().getStringList("Top-Body")) {
                commandSender.sendMessage(LocalMessageService.getInstance().convert(message
                        .replaceAll("%player%", playerName)
                        .replaceAll("%top%", String.valueOf(playerRecord.getSkillLevel(skill.getId(), false)))
                        .replaceAll("%rank%", String.valueOf(topPlayerRecords.indexOf(playerRecord) + 1))
                ));
            }
        }
        return true;
    }
}