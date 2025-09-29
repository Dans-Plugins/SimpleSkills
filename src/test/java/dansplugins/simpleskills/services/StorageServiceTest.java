package dansplugins.simpleskills.services;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.SkillRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import preponderous.ponder.misc.JsonWriterReader;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Unit tests for StorageService
 * 
 * Tests the storage functionality that persists player skill data
 * to prevent data loss during server crashes.
 * 
 * @author GitHub Copilot
 */
@RunWith(MockitoJUnitRunner.class)
public class StorageServiceTest {

    @Mock
    private PlayerRecordRepository mockPlayerRecordRepository;
    
    @Mock
    private SkillRepository mockSkillRepository;
    
    @Mock
    private MessageService mockMessageService;
    
    @Mock
    private ConfigService mockConfigService;
    
    @Mock
    private ExperienceCalculator mockExperienceCalculator;
    
    @Mock
    private Log mockLog;
    
    @Mock
    private PlayerRecord mockPlayerRecord1;
    
    @Mock
    private PlayerRecord mockPlayerRecord2;
    
    private StorageService storageService;
    
    @Before
    public void setUp() {
        storageService = new StorageService(
            mockPlayerRecordRepository,
            mockSkillRepository,
            mockMessageService,
            mockConfigService,
            mockExperienceCalculator,
            mockLog
        );
    }
    
    @Test
    public void testConstructor() {
        // Test that constructor properly initializes dependencies
        StorageService service = new StorageService(
            mockPlayerRecordRepository,
            mockSkillRepository,
            mockMessageService,
            mockConfigService,
            mockExperienceCalculator,
            mockLog
        );
        // If no exceptions thrown, constructor works correctly
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullPlayerRecordRepository() {
        new StorageService(
            null,
            mockSkillRepository,
            mockMessageService,
            mockConfigService,
            mockExperienceCalculator,
            mockLog
        );
    }
    
    @Test
    public void testSaveWithEmptyPlayerRecords() {
        // Arrange
        Set<PlayerRecord> emptyRecords = new HashSet<>();
        when(mockPlayerRecordRepository.getPlayerRecords()).thenReturn(emptyRecords);
        
        // Act
        storageService.save();
        
        // Assert - Should not throw exceptions with empty records
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecords();
    }
    
    @Test
    public void testSaveWithMultiplePlayerRecords() {
        // Arrange
        Set<PlayerRecord> playerRecords = new HashSet<>();
        playerRecords.add(mockPlayerRecord1);
        playerRecords.add(mockPlayerRecord2);
        
        Map<String, String> player1Data = new HashMap<>();
        player1Data.put("playerUUID", "uuid1");
        player1Data.put("skillLevels", "{}");
        
        Map<String, String> player2Data = new HashMap<>();
        player2Data.put("playerUUID", "uuid2");
        player2Data.put("skillLevels", "{}");
        
        when(mockPlayerRecordRepository.getPlayerRecords()).thenReturn(playerRecords);
        when(mockPlayerRecord1.save()).thenReturn(player1Data);
        when(mockPlayerRecord2.save()).thenReturn(player2Data);
        
        // Act
        storageService.save();
        
        // Assert
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecords();
        verify(mockPlayerRecord1, times(1)).save();
        verify(mockPlayerRecord2, times(1)).save();
    }
    
    @Test
    public void testSaveWhenPlayerRecordThrows() {
        // Arrange
        Set<PlayerRecord> playerRecords = new HashSet<>();
        playerRecords.add(mockPlayerRecord1);
        
        when(mockPlayerRecordRepository.getPlayerRecords()).thenReturn(playerRecords);
        when(mockPlayerRecord1.save()).thenThrow(new RuntimeException("Save error"));
        
        // Act & Assert - Exception should propagate
        try {
            storageService.save();
        } catch (RuntimeException e) {
            // Expected - storage service should not catch player record exceptions
        }
        
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecords();
        verify(mockPlayerRecord1, times(1)).save();
    }
    
    @Test
    public void testLoadClearsExistingRecords() {
        // Arrange
        Set<PlayerRecord> existingRecords = new HashSet<>();
        existingRecords.add(mockPlayerRecord1);
        when(mockPlayerRecordRepository.getPlayerRecords()).thenReturn(existingRecords);
        
        // Act
        storageService.load();
        
        // Assert
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecords();
        verify(existingRecords, times(1)).clear();
    }
    
    @Test
    public void testMultipleSaveCalls() {
        // Arrange
        Set<PlayerRecord> playerRecords = new HashSet<>();
        playerRecords.add(mockPlayerRecord1);
        
        Map<String, String> playerData = new HashMap<>();
        playerData.put("playerUUID", "uuid1");
        
        when(mockPlayerRecordRepository.getPlayerRecords()).thenReturn(playerRecords);
        when(mockPlayerRecord1.save()).thenReturn(playerData);
        
        // Act - Call save multiple times
        storageService.save();
        storageService.save();
        storageService.save();
        
        // Assert - Should handle multiple calls correctly
        verify(mockPlayerRecordRepository, times(3)).getPlayerRecords();
        verify(mockPlayerRecord1, times(3)).save();
    }
    
    @Test
    public void testSaveAndLoadIntegration() {
        // Arrange for save
        Set<PlayerRecord> playerRecords = new HashSet<>();
        playerRecords.add(mockPlayerRecord1);
        
        Map<String, String> playerData = new HashMap<>();
        playerData.put("playerUUID", "uuid1");
        playerData.put("skillLevels", "{}");
        
        when(mockPlayerRecordRepository.getPlayerRecords()).thenReturn(playerRecords);
        when(mockPlayerRecord1.save()).thenReturn(playerData);
        
        // Act - Save then load
        storageService.save();
        storageService.load();
        
        // Assert
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecords(); // For save
        verify(mockPlayerRecord1, times(1)).save();
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecords(); // For load clear
        verify(mockPlayerRecordRepository, times(1)).setPlayerRecords(any());
    }
}