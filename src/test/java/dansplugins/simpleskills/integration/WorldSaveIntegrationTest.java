package dansplugins.simpleskills.integration;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.listeners.WorldSaveEventListener;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.services.StorageService;
import dansplugins.simpleskills.skill.SkillRepository;
import org.bukkit.World;
import org.bukkit.event.world.WorldSaveEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Integration tests for WorldSaveEventListener with StorageService
 * 
 * Tests the complete flow from world save event to data persistence,
 * ensuring the implementation prevents data loss during server crashes.
 * 
 * @author GitHub Copilot
 */
@RunWith(MockitoJUnitRunner.class)
public class WorldSaveIntegrationTest {

    @Mock
    private PlayerRecordRepository mockPlayerRecordRepository;
    
    @Mock
    private SkillRepository mockSkillRepository;
    
    @Mock
    private MessageService mockMessageService;
    
    @Mock
    private ConfigService mockConfigService;
    
    @Mock
    private Log mockLog;
    
    @Mock
    private WorldSaveEvent mockWorldSaveEvent;
    
    @Mock
    private World mockWorld;
    
    @Mock
    private PlayerRecord mockPlayerRecord;
    
    private StorageService storageService;
    private WorldSaveEventListener worldSaveEventListener;
    private ExperienceCalculator experienceCalculator;
    
    @Before
    public void setUp() {
        experienceCalculator = new ExperienceCalculator();
        storageService = new StorageService(
            mockPlayerRecordRepository,
            mockSkillRepository,
            mockMessageService,
            mockConfigService,
            experienceCalculator,
            mockLog
        );
        worldSaveEventListener = new WorldSaveEventListener(storageService, mockLog);
        
        when(mockWorldSaveEvent.getWorld()).thenReturn(mockWorld);
        when(mockWorld.getName()).thenReturn("testworld");
    }
    
    @Test
    public void testWorldSaveTriggersDataPersistence() {
        // Arrange - Set up player records to be saved
        Set<PlayerRecord> playerRecords = new HashSet<>();
        playerRecords.add(mockPlayerRecord);
        
        when(mockPlayerRecordRepository.getPlayerRecords()).thenReturn(playerRecords);
        when(mockPlayerRecord.save()).thenReturn(java.util.Collections.singletonMap("playerUUID", UUID.randomUUID().toString()));
        
        // Act - Trigger world save event
        worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        
        // Assert - Verify complete flow
        verify(mockPlayerRecordRepository, times(1)).getPlayerRecords();
        verify(mockPlayerRecord, times(1)).save();
        verify(mockLog, times(1)).debug("World save event detected for world: testworld. Saving SimpleSkills plugin data.");
        verify(mockLog, times(1)).debug("SimpleSkills plugin data saved successfully during world save.");
    }
    
    @Test
    public void testRepeatedWorldSaves() {
        // Arrange
        Set<PlayerRecord> playerRecords = new HashSet<>();
        playerRecords.add(mockPlayerRecord);
        
        when(mockPlayerRecordRepository.getPlayerRecords()).thenReturn(playerRecords);
        when(mockPlayerRecord.save()).thenReturn(java.util.Collections.singletonMap("playerUUID", UUID.randomUUID().toString()));
        
        // Act - Multiple world saves (simulating server autosaves every 5 minutes)
        worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        
        // Assert - Each save should persist data
        verify(mockPlayerRecordRepository, times(3)).getPlayerRecords();
        verify(mockPlayerRecord, times(3)).save();
        verify(mockLog, times(3)).debug("World save event detected for world: testworld. Saving SimpleSkills plugin data.");
        verify(mockLog, times(3)).debug("SimpleSkills plugin data saved successfully during world save.");
    }
}