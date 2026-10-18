package dansplugins.simpleskills.listeners;

import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link PlayerJoinEventListener}'s creation of a player record for a player who
 * joins without one, and the error it logs and reports to the player when that record cannot be
 * created.
 * <p>
 * A real {@link PlayerJoinEvent} is constructed rather than mocked, as {@link PlacedBlockListenerTest}
 * and {@link WorldSaveEventListenerTest} also do, so that the event the listener receives is the one
 * the server would hand it.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PlayerJoinEventListenerTest {
    private static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock
    private PlayerRecordRepository playerRecordRepository;

    @Mock
    private Log log;

    @Mock
    private Player player;

    @Mock
    private PlayerRecord playerRecord;

    private PlayerJoinEventListener playerJoinEventListener;

    @Before
    public void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        when(player.getName()).thenReturn("Steve");
        playerJoinEventListener = new PlayerJoinEventListener(playerRecordRepository, log);
    }

    @Test
    public void onPlayerJoin_withAnExistingRecord_leavesItAlone() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(playerRecord);

        playerJoinEventListener.onPlayerJoin(new PlayerJoinEvent(player, "Steve joined the game"));

        verify(playerRecordRepository, never()).createPlayerRecord(any());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void onPlayerJoin_withoutARecord_createsOne() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(null);
        when(playerRecordRepository.createPlayerRecord(PLAYER_UUID)).thenReturn(true);

        playerJoinEventListener.onPlayerJoin(new PlayerJoinEvent(player, "Steve joined the game"));

        verify(playerRecordRepository).createPlayerRecord(PLAYER_UUID);
        verify(log).debug("No player record found for Steve. Creating a new one.");
        verify(log, never()).error(anyString());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void onPlayerJoin_whenTheRecordCannotBeCreated_logsAnErrorAndTellsThePlayer() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_UUID)).thenReturn(null);
        when(playerRecordRepository.createPlayerRecord(PLAYER_UUID)).thenReturn(false);

        playerJoinEventListener.onPlayerJoin(new PlayerJoinEvent(player, "Steve joined the game"));

        verify(log).error("Failed to create player record for Steve. Please check the database connection.");
        verify(player).sendMessage("Error creating SimpleSkills player record. Please try again later.");
    }
}
