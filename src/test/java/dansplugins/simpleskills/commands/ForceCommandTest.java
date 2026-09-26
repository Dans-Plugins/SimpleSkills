package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.SkillRepository;
import dansplugins.simpleskills.services.StorageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link ForceCommand}'s {@code /ss force wipe}, which clears every player record.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ForceCommandTest {

    @Mock
    private PlayerRecordRepository playerRecordRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ConfigService configService;

    @Mock
    private StorageService storageService;

    @Mock
    private CommandSender console;

    private HashSet<PlayerRecord> playerRecords;

    private ForceCommand forceCommand;

    @Before
    public void setUp() {
        playerRecords = new HashSet<>();
        playerRecords.add(mock(PlayerRecord.class));
        when(playerRecordRepository.getPlayerRecords()).thenAnswer(invocation -> playerRecords);
        when(console.hasPermission("ss.force.wipe")).thenReturn(true);

        forceCommand = new ForceCommand(playerRecordRepository, skillRepository, configService, storageService);
    }

    @Test
    public void wipe_clearsTheRecordsAndSavesTheEmptySetStraightAway() {
        final InOrder order = inOrder(playerRecordRepository, storageService);

        assertTrue(forceCommand.execute(console, new String[]{"wipe"}));

        assertEquals(0, playerRecords.size());
        // Saved after the clear, so what reaches disk is the wiped state rather than the old one.
        order.verify(playerRecordRepository).getPlayerRecords();
        order.verify(storageService).save();
        verify(console).sendMessage("Player records have been cleared.");
    }

    @Test
    public void wipe_neitherClearsNorSaves_whenRunByAPlayer() {
        final Player player = mock(Player.class);
        when(player.hasPermission("ss.force.wipe")).thenReturn(true);

        assertFalse(forceCommand.execute(player, new String[]{"wipe"}));

        assertEquals(1, playerRecords.size());
        verify(storageService, never()).save();
    }

    @Test
    public void wipe_neitherClearsNorSaves_withoutPermission() {
        when(console.hasPermission("ss.force.wipe")).thenReturn(false);

        assertFalse(forceCommand.execute(console, new String[]{"wipe"}));

        assertEquals(1, playerRecords.size());
        verify(storageService, never()).save();
    }
}
