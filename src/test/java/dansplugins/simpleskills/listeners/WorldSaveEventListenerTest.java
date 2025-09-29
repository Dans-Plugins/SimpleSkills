package dansplugins.simpleskills.listeners;

import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.services.StorageService;
import org.bukkit.World;
import org.bukkit.event.world.WorldSaveEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Unit tests for WorldSaveEventListener
 * 
 * Tests the implementation that saves plugin data during server autosaves
 * to prevent data loss during unexpected crashes.
 * 
 * @author GitHub Copilot
 */
@RunWith(MockitoJUnitRunner.class)
public class WorldSaveEventListenerTest {

    @Mock
    private StorageService mockStorageService;
    
    @Mock
    private Log mockLog;
    
    @Mock
    private WorldSaveEvent mockWorldSaveEvent;
    
    @Mock
    private World mockWorld;
    
    private WorldSaveEventListener worldSaveEventListener;
    
    @Before
    public void setUp() {
        worldSaveEventListener = new WorldSaveEventListener(mockStorageService, mockLog);
        when(mockWorldSaveEvent.getWorld()).thenReturn(mockWorld);
        when(mockWorld.getName()).thenReturn("world");
    }
    
    @Test
    public void testConstructor() {
        // Test that constructor properly initializes dependencies
        WorldSaveEventListener listener = new WorldSaveEventListener(mockStorageService, mockLog);
        // If no exceptions thrown, constructor works correctly
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullStorageService() {
        new WorldSaveEventListener(null, mockLog);
    }
    
    @Test(expected = NullPointerException.class) 
    public void testConstructorWithNullLog() {
        new WorldSaveEventListener(mockStorageService, null);
    }
    
    @Test
    public void testOnWorldSave() {
        // Act
        worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        
        // Assert
        verify(mockStorageService, times(1)).save();
        verify(mockLog, times(1)).debug("World save event detected for world: world. Saving SimpleSkills plugin data.");
        verify(mockLog, times(1)).debug("SimpleSkills plugin data saved successfully during world save.");
    }
    
    @Test
    public void testOnWorldSaveWithDifferentWorldName() {
        // Arrange
        when(mockWorld.getName()).thenReturn("nether");
        
        // Act
        worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        
        // Assert
        verify(mockStorageService, times(1)).save();
        verify(mockLog, times(1)).debug("World save event detected for world: nether. Saving SimpleSkills plugin data.");
        verify(mockLog, times(1)).debug("SimpleSkills plugin data saved successfully during world save.");
    }
    
    @Test
    public void testOnWorldSaveMultipleCalls() {
        // Act - Call multiple times to simulate multiple world saves
        worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        
        // Assert - Storage service should be called each time
        verify(mockStorageService, times(3)).save();
        verify(mockLog, times(3)).debug("World save event detected for world: world. Saving SimpleSkills plugin data.");
        verify(mockLog, times(3)).debug("SimpleSkills plugin data saved successfully during world save.");
    }
    
    @Test
    public void testOnWorldSaveWhenStorageServiceThrows() {
        // Arrange - Make storage service throw an exception
        doThrow(new RuntimeException("Storage error")).when(mockStorageService).save();
        
        // Act & Assert - Exception should propagate (not caught by listener)
        try {
            worldSaveEventListener.onWorldSave(mockWorldSaveEvent);
        } catch (RuntimeException e) {
            // Expected - listener should not catch storage exceptions
        }
        
        // Verify storage service was called and first debug message was logged
        verify(mockStorageService, times(1)).save();
        verify(mockLog, times(1)).debug("World save event detected for world: world. Saving SimpleSkills plugin data.");
        // Second debug message should not be called due to exception
        verify(mockLog, never()).debug("SimpleSkills plugin data saved successfully during world save.");
    }
    
    @Test(expected = NullPointerException.class)
    public void testOnWorldSaveWithNullEvent() {
        worldSaveEventListener.onWorldSave(null);
    }
}