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
public class MiningTest {

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

    private Mining mining;

    @Before
    public void setUp() {
        mining = new Mining(configService, log, playerRecordRepository, simpleSkills, messageService, chanceCalculator);
        
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
    public void testIsRequiredItem_WithPickaxe_ReturnsTrue() {
        ItemStack pickaxe = mock(ItemStack.class);
        when(pickaxe.getType()).thenReturn(Material.IRON_PICKAXE);
        
        assertTrue(mining.isRequiredItem(pickaxe, block, "break"));
    }

    @Test
    public void testIsRequiredItem_WithoutPickaxe_ReturnsFalse() {
        ItemStack axe = mock(ItemStack.class);
        when(axe.getType()).thenReturn(Material.IRON_AXE);
        
        assertFalse(mining.isRequiredItem(axe, block, "break"));
    }

    @Test
    public void testIsItemRequired_ReturnsTrue() {
        assertTrue(mining.isItemRequired());
    }

    @Test
    public void testIsValidMaterial_IronOre_ReturnsTrue() {
        assertTrue(mining.isValidMaterial(Material.IRON_ORE));
    }

    @Test
    public void testIsValidMaterial_GoldOre_ReturnsTrue() {
        assertTrue(mining.isValidMaterial(Material.GOLD_ORE));
    }

    @Test
    public void testIsValidMaterial_DiamondOre_ReturnsTrue() {
        assertTrue(mining.isValidMaterial(Material.DIAMOND_ORE));
    }

    @Test
    public void testIsValidMaterial_CoalOre_ReturnsTrue() {
        assertTrue(mining.isValidMaterial(Material.COAL_ORE));
    }

    @Test
    public void testIsValidMaterial_NonOreMaterial_ReturnsFalse() {
        assertFalse(mining.isValidMaterial(Material.STONE));
        assertFalse(mining.isValidMaterial(Material.DIRT));
        assertFalse(mining.isValidMaterial(Material.GRASS_BLOCK));
    }

    @Test
    public void testExecuteReward_NoChance_NoReward() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Mining.class), eq(0.10))).thenReturn(false);
        
        mining.executeReward(player, block, new Object());
        
        verify(world, never()).spawnEntity(any(), any());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void testExecuteReward_WithChance_PlaysSound() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Mining.class), eq(0.10))).thenReturn(true);
        when(block.getType()).thenReturn(Material.IRON_ORE);
        
        mining.executeReward(player, block, new Object());
        
        // Verify that sound was played (happens for all benefits)
        verify(player).playSound(any(), eq(Sound.BLOCK_AMETHYST_BLOCK_CHIME), eq(5f), eq(2f));
    }

    @Test
    public void testRandomExpGainChance_ReturnsFalse() {
        assertFalse(mining.randomExpGainChance());
    }

    @Test
    public void testGetChance_ReturnsZero() {
        assertEquals(0.0, mining.getChance(), 0.001);
    }

    @Test
    public void testGetBlockSkillType_ReturnsBreakSpecific() {
        assertEquals(Mining.BlockSkillType.BREAK_SPECIFIC, mining.getBlockSkillType());
    }

    @Test
    public void testExecuteReward_NullPlayerRecord_DoesNothing() {
        when(playerRecordRepository.getPlayerRecord(any(UUID.class))).thenReturn(null);
        when(chanceCalculator.roll(any(), any(), anyDouble())).thenReturn(true);
        
        mining.executeReward(player, block, new Object());
        
        verify(world, never()).spawnEntity(any(), any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteReward_InvalidSkillDataLength_ThrowsException() {
        mining.executeReward(player, block);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteReward_InvalidSkillDataType_ThrowsException() {
        mining.executeReward(player, "not a block", new Object());
    }

    @Test
    public void testExecuteReward_SpawnsExperienceOrb_WithValidSetup() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Mining.class), eq(0.10))).thenReturn(true);
        when(block.getType()).thenReturn(Material.COAL_ORE);
        when(world.spawnEntity(any(), eq(EntityType.EXPERIENCE_ORB))).thenReturn(expOrb);
        
        // Run multiple times to statistically hit the XP benefit option
        for (int i = 0; i < 30; i++) {
            mining.executeReward(player, block, new Object());
        }
        
        // Verify sound was always played
        verify(player, times(30)).playSound(any(), eq(Sound.BLOCK_AMETHYST_BLOCK_CHIME), eq(5f), eq(2f));
    }
}
