package dansplugins.simpleskills.playerrecord;

import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.experience.ExperienceCalculator;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.services.StorageService;
import dansplugins.simpleskills.skill.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PlayerRecordRepository with auto-save integration
 */
class PlayerRecordRepositoryAutoSaveTest {

    @Mock
    private Log log;
    @Mock
    private MessageService messageService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private ConfigService configService;
    @Mock
    private ExperienceCalculator experienceCalculator;
    @Mock
    private StorageService storageService;

    private PlayerRecordRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new PlayerRecordRepository(log, messageService, skillRepository, 
                                               configService, experienceCalculator);
    }

    @Test
    void testRepositoryCreation() {
        assertNotNull(repository);
        assertNotNull(repository.getPlayerRecords());
        assertEquals(0, repository.getPlayerRecords().size());
    }

    @Test
    void testSetStorageServiceMethod() {
        // Verify that setStorageService method exists and can be called
        assertDoesNotThrow(() -> {
            repository.setStorageService(storageService);
        });
    }

    @Test
    void testStorageServiceInjection() throws Exception {
        // Given: Repository with injected StorageService
        repository.setStorageService(storageService);
        
        // When: Creating a new player record
        UUID testUUID = UUID.randomUUID();
        boolean created = repository.createPlayerRecord(testUUID);

        // Then: Record should be created successfully
        assertTrue(created);
        assertEquals(1, repository.getPlayerRecords().size());
        
        // And: The created record should have StorageService
        PlayerRecord record = repository.getPlayerRecord(testUUID);
        assertNotNull(record);
        
        Field storageServiceField = PlayerRecord.class.getDeclaredField("storageService");
        storageServiceField.setAccessible(true);
        Object injectedService = storageServiceField.get(record);
        assertEquals(storageService, injectedService);
    }

    @Test
    void testCreatePlayerRecordWithoutStorageService() {
        // Given: Repository without StorageService injected
        UUID testUUID = UUID.randomUUID();
        
        // When: Creating a player record (should not crash even without StorageService)
        boolean created = repository.createPlayerRecord(testUUID);

        // Then: Record should still be created (StorageService can be null)
        assertTrue(created);
        assertEquals(1, repository.getPlayerRecords().size());
    }

    @Test
    void testGetPlayerRecord() {
        // Given: Repository with a player record
        UUID testUUID = UUID.randomUUID();
        repository.createPlayerRecord(testUUID);
        
        // When: Getting the player record
        PlayerRecord record = repository.getPlayerRecord(testUUID);

        // Then: Record should be found
        assertNotNull(record);
        assertEquals(testUUID, record.getPlayerUUID());
    }

    @Test
    void testGetNonExistentPlayerRecord() {
        // Given: Repository with no records
        UUID testUUID = UUID.randomUUID();
        
        // When: Getting a non-existent player record
        PlayerRecord record = repository.getPlayerRecord(testUUID);

        // Then: Should return null
        assertNull(record);
    }
}
