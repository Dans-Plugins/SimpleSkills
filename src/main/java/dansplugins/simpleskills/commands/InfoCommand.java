package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.SkillRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class InfoCommand extends AbstractPluginCommand {
    private final PlayerRecordRepository playerRecordRepository;
    private final MessageService messageService;
    private final SkillRepository skillRepository;
    private final ConfigService configService;
    private final ExperienceCalculator experienceCalculator;
    private final Log log;

    public InfoCommand(PlayerRecordRepository playerRecordRepository, MessageService messageService, SkillRepository skillRepository, ConfigService configService, ExperienceCalculator experienceCalculator, Log log) {
        super(
                new ArrayList<>(Collections.singletonList("info")),
                new ArrayList<>(Collections.singletonList("ss.info"))
        );
        this.playerRecordRepository = playerRecordRepository;
        this.messageService = messageService;
        this.skillRepository = skillRepository;
        this.configService = configService;
        this.experienceCalculator = experienceCalculator;
        this.log = log;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;
        PlayerRecord playerRecord = playerRecordRepository.getPlayerRecord(player.getUniqueId());
        if (playerRecord == null) {
            log.debug("No player record found for " + player.getName() + ". Creating a new one.");
            boolean success = playerRecordRepository.createPlayerRecord(player.getUniqueId());
            if (!success) {
                commandSender.sendMessage("Error creating player record. Please try again later.");
                return false;
            }
            playerRecord = playerRecordRepository.getPlayerRecord(player.getUniqueId());
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String playerName = args[0];
        UUID playerUUID = new UUIDChecker().findUUIDBasedOnPlayerName(playerName);
        if (playerUUID == null) {
            commandSender.sendMessage(messageService.convert(messageService.getlang().getString("NotFound")));
            return false;
        }
        PlayerRecord playerRecord = playerRecordRepository.getPlayerRecord(playerUUID);
        if (playerRecord == null) {
            boolean success = playerRecordRepository.createPlayerRecord(playerUUID);
            if (!success) {
                commandSender.sendMessage("Error creating player record for " + playerName + ". Please try again later.");
                return false;
            }
            playerRecord = playerRecordRepository.getPlayerRecord(playerUUID);
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }
}