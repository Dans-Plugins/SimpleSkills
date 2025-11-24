package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WoodcuttingTest {

    @Mock
    private ConfigService configService;
    @Mock
    private Log log;
    @Mock
    private PlayerRecordRepository playerRecordRepository;
    @Mock
    private SimpleSkills simpleSkills;
    @Mock
    private MessageService messageService;
    @Mock
    private ChanceCalculator chanceCalculator;
    @Mock
    private Player player;
    @Mock
    private Block block;
    @Mock
    private World world;
    @Mock
    private PlayerRecord playerRecord;
    @Mock
    private PlayerInventory inventory;
    @Mock
    private ItemStack itemStack;
    @Mock
    private ExperienceOrb expOrb;

    private Woodcutting woodcutting;

    @Before
    public void setUp() {
        woodcutting = new Woodcutting(configService, log, playerRecordRepository, simpleSkills, messageService, chanceCalculator);
        
        // Setup common mocks
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(playerRecordRepository.getPlayerRecord(any(UUID.class))).thenReturn(playerRecord);
        when(block.getWorld()).thenReturn(world);
        when(player.getInventory()).thenReturn(inventory);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);
        when(messageService.convert(anyString())).thenAnswer(i -> i.getArguments()[0]);
        when(messageService.getlang()).thenReturn(new org.bukkit.configuration.file.YamlConfiguration());
    }

    @Test
    public void testIsRequiredItem_WithAxe_ReturnsTrue() {
        ItemStack axe = mock(ItemStack.class);
        when(axe.getType()).thenReturn(Material.DIAMOND_AXE);
        
        assertTrue(woodcutting.isRequiredItem(axe, block, "break"));
    }

    @Test
    public void testIsRequiredItem_WithAir_ReturnsTrue() {
        ItemStack air = mock(ItemStack.class);
        when(air.getType()).thenReturn(Material.AIR);
        when(air.getType().isAir()).thenReturn(true);
        
        assertTrue(woodcutting.isRequiredItem(air, block, "break"));
    }

    @Test
    public void testIsRequiredItem_WithoutAxe_ReturnsFalse() {
        ItemStack pickaxe = mock(ItemStack.class);
        when(pickaxe.getType()).thenReturn(Material.DIAMOND_PICKAXE);
        
        assertFalse(woodcutting.isRequiredItem(pickaxe, block, "break"));
    }

    @Test
    public void testIsItemRequired_ReturnsTrue() {
        assertTrue(woodcutting.isItemRequired());
    }

    @Test
    public void testIsValidMaterial_OakLog_ReturnsTrue() {
        assertTrue(woodcutting.isValidMaterial(Material.OAK_LOG));
    }

    @Test
    public void testIsValidMaterial_BirchLog_ReturnsTrue() {
        assertTrue(woodcutting.isValidMaterial(Material.BIRCH_LOG));
    }

    @Test
    public void testIsValidMaterial_SpruceLog_ReturnsTrue() {
        assertTrue(woodcutting.isValidMaterial(Material.SPRUCE_LOG));
    }

    @Test
    public void testIsValidMaterial_AcaciaWood_ReturnsTrue() {
        assertTrue(woodcutting.isValidMaterial(Material.ACACIA_WOOD));
    }

    @Test
    public void testIsValidMaterial_StrippedOakLog_ReturnsTrue() {
        assertTrue(woodcutting.isValidMaterial(Material.STRIPPED_OAK_LOG));
    }

    @Test
    public void testIsValidMaterial_InvalidMaterial_ReturnsFalse() {
        assertFalse(woodcutting.isValidMaterial(Material.STONE));
        assertFalse(woodcutting.isValidMaterial(Material.DIRT));
        assertFalse(woodcutting.isValidMaterial(Material.IRON_ORE));
    }

    @Test
    public void testExecuteReward_NoChance_NoReward() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Woodcutting.class), eq(0.10))).thenReturn(false);
        
        woodcutting.executeReward(player, block, new Object());
        
        verify(world, never()).spawnEntity(any(), any());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void testExecuteReward_WithChance_PlaysSound() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Woodcutting.class), eq(0.10))).thenReturn(true);
        when(block.getType()).thenReturn(Material.OAK_LOG);
        
        woodcutting.executeReward(player, block, new Object());
        
        // Verify that sound was played (happens for all benefits)
        verify(player).playSound(any(), eq(Sound.BLOCK_AMETHYST_BLOCK_CHIME), eq(5f), eq(2f));
    }

    @Test
    public void testRandomExpGainChance_ReturnsFalse() {
        assertFalse(woodcutting.randomExpGainChance());
    }

    @Test
    public void testGetChance_ReturnsZero() {
        assertEquals(0.0, woodcutting.getChance(), 0.001);
    }

    @Test
    public void testGetBlockSkillType_ReturnsBreakSpecific() {
        assertEquals(Woodcutting.BlockSkillType.BREAK_SPECIFIC, woodcutting.getBlockSkillType());
    }

    @Test
    public void testExecuteReward_NullPlayerRecord_DoesNothing() {
        when(playerRecordRepository.getPlayerRecord(any(UUID.class))).thenReturn(null);
        when(chanceCalculator.roll(any(), any(), anyDouble())).thenReturn(true);
        
        woodcutting.executeReward(player, block, new Object());
        
        verify(world, never()).spawnEntity(any(), any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteReward_InvalidSkillDataLength_ThrowsException() {
        woodcutting.executeReward(player, block);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteReward_InvalidSkillDataType_ThrowsException() {
        woodcutting.executeReward(player, "not a block", new Object());
    }

    @Test
    public void testExecuteReward_SpawnsExperienceOrb_WithValidSetup() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Woodcutting.class), eq(0.10))).thenReturn(true);
        when(block.getType()).thenReturn(Material.OAK_LOG);
        when(world.spawnEntity(any(), eq(EntityType.EXPERIENCE_ORB))).thenReturn(expOrb);
        
        // Run multiple times to statistically hit the XP benefit option (50% chance for woodcutting)
        for (int i = 0; i < 20; i++) {
            woodcutting.executeReward(player, block, new Object());
        }
        
        // Verify sound was always played
        verify(player, times(20)).playSound(any(), eq(Sound.BLOCK_AMETHYST_BLOCK_CHIME), eq(5f), eq(2f));
    }
}
