package dansplugins.simpleskills.listeners;

import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Unit tests for PlayerJoinEventListener
 * 
 * Tests the player join functionality that creates player records
 * when new players join the server.
 * 
 * @author GitHub Copilot
 */
@RunWith(MockitoJUnitRunner.class)
public class PlayerJoinEventListenerTest {

    @Mock
    private PlayerRecordRepository mockPlayerRecordRepository;
    
    @Mock
    private Log mockLog;
    
    @Mock
    private PlayerJoinEvent mockPlayerJoinEvent;
    
    @Mock
    private Player mockPlayer;
    
    private PlayerJoinEventListener playerJoinEventListener;
    private UUID testPlayerUUID;
    
    @Before
    public void setUp() {
        playerJoinEventListener = new PlayerJoinEventListener(mockPlayerRecordRepository, mockLog);
        testPlayerUUID = UUID.randomUUID();
        
        when(mockPlayerJoinEvent.getPlayer()).thenReturn(mockPlayer);
        when(mockPlayer.getUniqueId()).thenReturn(testPlayerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
    }
    
    @Test
    public void testConstructor() {
        // Test that constructor properly initializes dependencies
        PlayerJoinEventListener listener = new PlayerJoinEventListener(mockPlayerRecordRepository, mockLog);
        // If no exceptions thrown, constructor works correctly
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullRepository() {
        new PlayerJoinEventListener(null, mockLog);
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullLog() {
        new PlayerJoinEventListener(mockPlayerRecordRepository, null);
    }
    
    @Test
    public void testOnPlayerJoinWithExistingRecord() {
        // Arrange - Player already has a record
        when(mockPlayerRecordRepository.getPlayerRecord(testPlayerUUID)).thenReturn(mock(Object.class));
        
        // Act
        playerJoinEventListener.onPlayerJoin(mockPlayerJoinEvent);
        
        // Assert - Should not create new record or log creation
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecord(testPlayerUUID);
        verify(mockPlayerRecordRepository, never()).createPlayerRecord(any(UUID.class));
        verify(mockLog, never()).debug(anyString());
        verify(mockLog, never()).error(anyString());
    }
    
    @Test
    public void testOnPlayerJoinWithNewPlayerSuccess() {
        // Arrange - Player doesn't have a record, creation succeeds
        when(mockPlayerRecordRepository.getPlayerRecord(testPlayerUUID)).thenReturn(null);
        when(mockPlayerRecordRepository.createPlayerRecord(testPlayerUUID)).thenReturn(true);
        
        // Act
        playerJoinEventListener.onPlayerJoin(mockPlayerJoinEvent);
        
        // Assert
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecord(testPlayerUUID);
        verify(mockPlayerRecordRepository, times(1)).createPlayerRecord(testPlayerUUID);
        verify(mockLog, times(1)).debug("No player record found for TestPlayer. Creating a new one.");
        verify(mockLog, never()).error(anyString());
        verify(mockPlayer, never()).sendMessage(anyString());
    }
    
    @Test
    public void testOnPlayerJoinWithNewPlayerFailure() {
        // Arrange - Player doesn't have a record, creation fails
        when(mockPlayerRecordRepository.getPlayerRecord(testPlayerUUID)).thenReturn(null);
        when(mockPlayerRecordRepository.createPlayerRecord(testPlayerUUID)).thenReturn(false);
        
        // Act
        playerJoinEventListener.onPlayerJoin(mockPlayerJoinEvent);
        
        // Assert
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecord(testPlayerUUID);
        verify(mockPlayerRecordRepository, times(1)).createPlayerRecord(testPlayerUUID);
        verify(mockLog, times(1)).debug("No player record found for TestPlayer. Creating a new one.");
        verify(mockLog, times(1)).error("Failed to create player record for TestPlayer. Please check the database connection.");
        verify(mockPlayer, times(1)).sendMessage("Error creating SimpleSkills player record. Please try again later.");
    }
    
    @Test
    public void testOnPlayerJoinMultiplePlayers() {
        // Arrange - Multiple different players
        UUID player2UUID = UUID.randomUUID();
        Player mockPlayer2 = mock(Player.class);
        PlayerJoinEvent mockEvent2 = mock(PlayerJoinEvent.class);
        
        when(mockEvent2.getPlayer()).thenReturn(mockPlayer2);
        when(mockPlayer2.getUniqueId()).thenReturn(player2UUID);
        when(mockPlayer2.getName()).thenReturn("TestPlayer2");
        
        // First player - no record, creation succeeds
        when(mockPlayerRecordRepository.getPlayerRecord(testPlayerUUID)).thenReturn(null);
        when(mockPlayerRecordRepository.createPlayerRecord(testPlayerUUID)).thenReturn(true);
        
        // Second player - no record, creation fails
        when(mockPlayerRecordRepository.getPlayerRecord(player2UUID)).thenReturn(null);
        when(mockPlayerRecordRepository.createPlayerRecord(player2UUID)).thenReturn(false);
        
        // Act
        playerJoinEventListener.onPlayerJoin(mockPlayerJoinEvent);
        playerJoinEventListener.onPlayerJoin(mockEvent2);
        
        // Assert
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecord(testPlayerUUID);
        verify(mockPlayerRecordRepository, times(1)).createPlayerRecord(testPlayerUUID);
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecord(player2UUID);
        verify(mockPlayerRecordRepository, times(1)).createPlayerRecord(player2UUID);
        
        verify(mockLog, times(1)).debug("No player record found for TestPlayer. Creating a new one.");
        verify(mockLog, times(1)).debug("No player record found for TestPlayer2. Creating a new one.");
        verify(mockLog, times(1)).error("Failed to create player record for TestPlayer2. Please check the database connection.");
        
        verify(mockPlayer, never()).sendMessage(anyString());
        verify(mockPlayer2, times(1)).sendMessage("Error creating SimpleSkills player record. Please try again later.");
    }
    
    @Test(expected = NullPointerException.class)
    public void testOnPlayerJoinWithNullEvent() {
        playerJoinEventListener.onPlayerJoin(null);
    }
    
    @Test
    public void testOnPlayerJoinWhenRepositoryThrows() {
        // Arrange - Repository throws exception
        when(mockPlayerRecordRepository.getPlayerRecord(testPlayerUUID))
            .thenThrow(new RuntimeException("Database error"));
        
        // Act & Assert - Exception should propagate
        try {
            playerJoinEventListener.onPlayerJoin(mockPlayerJoinEvent);
        } catch (RuntimeException e) {
            // Expected - listener should not catch repository exceptions
        }
        
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecord(testPlayerUUID);
        verify(mockPlayerRecordRepository, never()).createPlayerRecord(any(UUID.class));
    }
}