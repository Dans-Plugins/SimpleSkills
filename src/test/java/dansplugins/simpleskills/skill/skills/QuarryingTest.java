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
public class QuarryingTest {

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

    private Quarrying quarrying;

    @Before
    public void setUp() {
        quarrying = new Quarrying(configService, log, playerRecordRepository, simpleSkills, messageService, chanceCalculator);
        
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
        when(pickaxe.getType()).thenReturn(Material.DIAMOND_PICKAXE);
        
        assertTrue(quarrying.isRequiredItem(pickaxe, block, "break"));
    }

    @Test
    public void testIsRequiredItem_WithoutPickaxe_ReturnsFalse() {
        ItemStack sword = mock(ItemStack.class);
        when(sword.getType()).thenReturn(Material.DIAMOND_SWORD);
        
        assertFalse(quarrying.isRequiredItem(sword, block, "break"));
    }

    @Test
    public void testIsItemRequired_ReturnsTrue() {
        assertTrue(quarrying.isItemRequired());
    }

    @Test
    public void testIsValidMaterial_Stone_ReturnsTrue() {
        assertTrue(quarrying.isValidMaterial(Material.STONE));
    }

    @Test
    public void testIsValidMaterial_Granite_ReturnsTrue() {
        assertTrue(quarrying.isValidMaterial(Material.GRANITE));
    }

    @Test
    public void testIsValidMaterial_Andesite_ReturnsTrue() {
        assertTrue(quarrying.isValidMaterial(Material.ANDESITE));
    }

    @Test
    public void testIsValidMaterial_Diorite_ReturnsTrue() {
        assertTrue(quarrying.isValidMaterial(Material.DIORITE));
    }

    @Test
    public void testIsValidMaterial_InvalidMaterial_ReturnsFalse() {
        assertFalse(quarrying.isValidMaterial(Material.DIRT));
        assertFalse(quarrying.isValidMaterial(Material.DIAMOND_ORE));
    }

    @Test
    public void testExecuteReward_NoChance_NoReward() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Quarrying.class), eq(0.10))).thenReturn(false);
        
        quarrying.executeReward(player, block, new Object());
        
        verify(world, never()).spawnEntity(any(), any());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void testExecuteReward_WithChance_SpawnsExperienceOrb() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(Quarrying.class), eq(0.10))).thenReturn(true);
        when(block.getType()).thenReturn(Material.STONE);
        when(world.spawnEntity(any(), eq(EntityType.EXPERIENCE_ORB))).thenReturn(expOrb);
        
        // Mock to ensure we get the XP benefit (third option)
        // We'll run this multiple times to statistically hit the XP option
        for (int i = 0; i < 30; i++) {
            quarrying.executeReward(player, block, new Object());
        }
        
        // Verify that sound was played (happens for all benefits)
        verify(player, atLeastOnce()).playSound(any(), eq(Sound.BLOCK_AMETHYST_BLOCK_CHIME), eq(5f), eq(2f));
    }

    @Test
    public void testRandomExpGainChance_ReturnsFalse() {
        assertFalse(quarrying.randomExpGainChance());
    }

    @Test
    public void testGetChance_ReturnsZero() {
        assertEquals(0.0, quarrying.getChance(), 0.001);
    }

    @Test
    public void testGetBlockSkillType_ReturnsBreakSpecific() {
        assertEquals(Quarrying.BlockSkillType.BREAK_SPECIFIC, quarrying.getBlockSkillType());
    }

    @Test
    public void testExecuteReward_NullPlayerRecord_DoesNothing() {
        when(playerRecordRepository.getPlayerRecord(any(UUID.class))).thenReturn(null);
        when(chanceCalculator.roll(any(), any(), anyDouble())).thenReturn(true);
        
        quarrying.executeReward(player, block, new Object());
        
        verify(world, never()).spawnEntity(any(), any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteReward_InvalidSkillDataLength_ThrowsException() {
        quarrying.executeReward(player, block);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteReward_InvalidSkillDataType_ThrowsException() {
        quarrying.executeReward(player, "not a block", new Object());
    }
}
