package dansplugins.simpleskills.listeners;

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes {@link PlacedBlockListener}'s tracking of player-placed blocks, which
 * {@link dansplugins.simpleskills.skill.abs.AbstractBlockSkill} consults to prevent the
 * place-then-rebreak experience farming exploit (e.g. Silk Touch-harvested ore).
 * <p>
 * Real {@link BlockBreakEvent}/{@link BlockPlaceEvent} instances are constructed (rather than
 * mocked) because {@code BlockEvent#getBlock()} is {@code final} and mockito-core (without
 * mockito-inline) cannot stub final methods.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PlacedBlockListenerTest {
    private static final String METADATA_KEY = "simpleskills-player-placed";

    @Mock
    private SimpleSkills simpleSkills;

    @Mock
    private Block block;

    @Mock
    private Player player;

    private PlacedBlockListener placedBlockListener;

    @Before
    public void setUp() {
        placedBlockListener = new PlacedBlockListener(simpleSkills);
    }

    @Test
    public void onBlockPlace_marksTheBlockAsPlayerPlaced() {
        final BlockPlaceEvent event = new BlockPlaceEvent(block, mock(BlockState.class), block,
                mock(ItemStack.class), player, true, EquipmentSlot.HAND);

        placedBlockListener.onBlockPlace(event);

        final ArgumentCaptor<MetadataValue> valueCaptor = ArgumentCaptor.forClass(MetadataValue.class);
        verify(block).setMetadata(eq(METADATA_KEY), valueCaptor.capture());
        assertEquals(simpleSkills, valueCaptor.getValue().getOwningPlugin());
        assertTrue(valueCaptor.getValue().asBoolean());
    }

    @Test
    public void onBlockBreak_removesTrackingMetadata_whenBlockWasPlayerPlaced() {
        when(block.hasMetadata(METADATA_KEY)).thenReturn(true);
        final BlockBreakEvent event = new BlockBreakEvent(block, player);

        placedBlockListener.onBlockBreak(event);

        verify(block).removeMetadata(METADATA_KEY, simpleSkills);
    }

    @Test
    public void onBlockBreak_doesNothing_whenBlockWasNotPlayerPlaced() {
        when(block.hasMetadata(METADATA_KEY)).thenReturn(false);
        final BlockBreakEvent event = new BlockBreakEvent(block, player);

        placedBlockListener.onBlockBreak(event);

        verify(block, never()).removeMetadata(anyString(), any());
    }

    @Test
    public void isPlayerPlaced_reflectsWhetherTheMetadataIsPresent() {
        when(block.hasMetadata(METADATA_KEY)).thenReturn(true);
        assertTrue(PlacedBlockListener.isPlayerPlaced(block));

        when(block.hasMetadata(METADATA_KEY)).thenReturn(false);
        assertFalse(PlacedBlockListener.isPlayerPlaced(block));
    }
}
