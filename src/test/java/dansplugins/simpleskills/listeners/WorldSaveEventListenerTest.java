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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link WorldSaveEventListener}'s persistence of player records whenever the server
 * saves a world, and the window over which the per-world burst of events a single server save
 * produces is coalesced into one write.
 * <p>
 * A real {@link WorldSaveEvent} is constructed (rather than mocked) for the same reason
 * {@link PlacedBlockListenerTest} does so: {@code WorldEvent#getWorld()} is inherited rather than
 * declared on the event, and constructing the event exercises the same path the server takes.
 * The clock is substituted through the package-private {@code currentTimeMillis()} seam so that
 * the coalescing window can be crossed without waiting for it to pass.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class WorldSaveEventListenerTest {
    @Mock
    private StorageService storageService;

    @Mock
    private Log log;

    @Mock
    private World world;

    private long currentTimeMillis;
    private WorldSaveEventListener worldSaveEventListener;

    @Before
    public void setUp() {
        when(world.getName()).thenReturn("world");
        currentTimeMillis = 0L;
        worldSaveEventListener = new WorldSaveEventListener(storageService, log) {
            @Override
            long currentTimeMillis() {
                return currentTimeMillis;
            }
        };
    }

    @Test
    public void onWorldSave_savesThePluginData() {
        worldSaveEventListener.onWorldSave(new WorldSaveEvent(world));

        verify(storageService).save();
    }

    @Test
    public void onWorldSave_savesTheFirstTime_evenWhenTheClockReadsZero() {
        currentTimeMillis = 0L;

        worldSaveEventListener.onWorldSave(new WorldSaveEvent(world));

        verify(storageService).save();
    }

    @Test
    public void onWorldSave_savesOnlyOnce_forABurstOfEventsWithinTheCoalescingWindow() {
        currentTimeMillis = 1_000_000L;
        worldSaveEventListener.onWorldSave(new WorldSaveEvent(world));

        currentTimeMillis += WorldSaveEventListener.MINIMUM_SAVE_INTERVAL_MILLIS - 1;
        worldSaveEventListener.onWorldSave(new WorldSaveEvent(world));

        verify(storageService, times(1)).save();
    }

    @Test
    public void onWorldSave_savesAgain_onceTheCoalescingWindowHasPassed() {
        currentTimeMillis = 1_000_000L;
        worldSaveEventListener.onWorldSave(new WorldSaveEvent(world));

        currentTimeMillis += WorldSaveEventListener.MINIMUM_SAVE_INTERVAL_MILLIS;
        worldSaveEventListener.onWorldSave(new WorldSaveEvent(world));

        verify(storageService, times(2)).save();
    }
}
