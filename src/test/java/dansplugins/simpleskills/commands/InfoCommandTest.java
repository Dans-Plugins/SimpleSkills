package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.SkillRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link InfoCommand#execute(CommandSender)}, {@code /ss info} without arguments,
 * which shows the sender their own skills and creates their player record first if they have none.
 * <p>
 * {@code /ss info <player>} is not covered: it resolves the name through Ponder's
 * {@code UUIDChecker}, which reads Bukkit's static server accessors, and mockito-core (without
 * mockito-inline) cannot stub those.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class InfoCommandTest {

    private static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private PlayerRecordRepository playerRecordRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ConfigService configService;

    @Mock
    private ExperienceCalculator experienceCalculator;

    @Mock
    private Log log;

    @Mock
    private Player player;

    @Mock
    private CommandSender console;

    @Mock
    private PlayerRecord playerRecord;

    private InfoCommand infoCommand;

    @Before
    public void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        when(player.getName()).thenReturn("Alice");

        infoCommand = new InfoCommand(playerRecordRepository, messageService, skillRepository, configService, experienceCalculator, log);
    }

    @Test
    public void execute_rejectsSenderWhoIsNotAPlayer() {
        boolean result = infoCommand.execute(console);

        assertFalse(result);
        verify(console).sendMessage("Only players can use this command.");
        verifyNoInteractions(playerRecordRepository);
    }

    @Test
    public void execute_sendsTheExistingRecordsInfo_withoutCreatingANewRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(playerRecord);

        boolean result = infoCommand.execute(player);

        assertTrue(result);
        verify(playerRecord).sendInfo(player);
        verify(playerRecordRepository, never()).createPlayerRecord(any());
    }

    @Test
    public void execute_createsAMissingRecord_thenSendsItsInfo() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(null, playerRecord);
        when(playerRecordRepository.createPlayerRecord(PLAYER_UUID)).thenReturn(true);

        boolean result = infoCommand.execute(player);

        assertTrue(result);
        verify(playerRecordRepository).createPlayerRecord(PLAYER_UUID);
        verify(log).debug("No player record found for Alice. Creating a new one.");
        verify(playerRecord).sendInfo(player);
    }

    @Test
    public void execute_reportsAnError_whenTheMissingRecordCannotBeCreated() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(null);
        when(playerRecordRepository.createPlayerRecord(PLAYER_UUID)).thenReturn(false);

        boolean result = infoCommand.execute(player);

        assertFalse(result);
        verify(player).sendMessage("Error creating player record. Please try again later.");
        verify(playerRecord, never()).sendInfo(any());
    }
}
