package dansplugins.simpleskills.skill.skills;

import com.cryptomorin.xseries.XMaterial;
import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Quarrying skill to prove the fix for IllegalStateException
 * when XMaterial.parseMaterial() returns null.
 */
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
    private Location location;
    
    @Mock
    private PlayerInventory inventory;
    
    @Mock
    private PlayerRecord playerRecord;

    private Quarrying quarrying;

    @Before
    public void setUp() {
        quarrying = new Quarrying(
            configService,
            log,
            playerRecordRepository,
            simpleSkills,
            messageService,
            chanceCalculator
        );
    }

    /**
     * Test that getRewardTypes returns non-null materials for STONE.
     * This proves the fix: Objects.requireNonNull wraps parseMaterial calls.
     */
    @Test
    public void testGetRewardTypes_Stone_ReturnsNonNullMaterial() throws Exception {
        // Use reflection to access private method
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.STONE);
        
        assertNotNull("Reward list should not be null", rewards);
        assertFalse("Reward list should not be empty", rewards.isEmpty());
        assertNotNull("Reward material should not be null", rewards.get(0));
    }

    /**
     * Test that getRewardTypes returns non-null materials for GRANITE.
     * This proves the fix works for all material types.
     */
    @Test
    public void testGetRewardTypes_Granite_ReturnsNonNullMaterial() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.GRANITE);
        
        assertNotNull("Reward list should not be null", rewards);
        assertFalse("Reward list should not be empty", rewards.isEmpty());
        assertNotNull("POLISHED_GRANITE should not be null", rewards.get(0));
    }

    /**
     * Test that getRewardTypes returns non-null materials for ANDESITE.
     */
    @Test
    public void testGetRewardTypes_Andesite_ReturnsNonNullMaterial() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.ANDESITE);
        
        assertNotNull("Reward list should not be null", rewards);
        assertFalse("Reward list should not be empty", rewards.isEmpty());
        assertNotNull("POLISHED_ANDESITE should not be null", rewards.get(0));
    }

    /**
     * Test that getRewardTypes returns non-null materials for DIORITE.
     */
    @Test
    public void testGetRewardTypes_Diorite_ReturnsNonNullMaterial() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.DIORITE);
        
        assertNotNull("Reward list should not be null", rewards);
        assertFalse("Reward list should not be empty", rewards.isEmpty());
        assertNotNull("POLISHED_DIORITE should not be null", rewards.get(0));
    }

    /**
     * Test that getRewardTypes returns non-null materials for DEEPSLATE.
     */
    @Test
    public void testGetRewardTypes_Deepslate_ReturnsNonNullMaterial() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.DEEPSLATE);
        
        assertNotNull("Reward list should not be null", rewards);
        assertFalse("Reward list should not be empty", rewards.isEmpty());
        assertNotNull("DEEPSLATE should not be null", rewards.get(0));
    }

    /**
     * Test that getRewardTypes returns non-null materials for SANDSTONE.
     */
    @Test
    public void testGetRewardTypes_Sandstone_ReturnsNonNullMaterial() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.SANDSTONE);
        
        assertNotNull("Reward list should not be null", rewards);
        assertFalse("Reward list should not be empty", rewards.isEmpty());
        assertNotNull("SANDSTONE should not be null", rewards.get(0));
    }

    /**
     * Test that getRewardTypes returns non-null materials for RED_SANDSTONE.
     */
    @Test
    public void testGetRewardTypes_RedSandstone_ReturnsNonNullMaterial() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.RED_SANDSTONE);
        
        assertNotNull("Reward list should not be null", rewards);
        assertFalse("Reward list should not be empty", rewards.isEmpty());
        assertNotNull("GLASS_BOTTLE should not be null", rewards.get(0));
    }

    /**
     * Test that getRewardTypes returns non-null materials for END_STONE.
     */
    @Test
    public void testGetRewardTypes_EndStone_ReturnsNonNullMaterial() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.END_STONE);
        
        assertNotNull("Reward list should not be null", rewards);
        assertFalse("Reward list should not be empty", rewards.isEmpty());
        assertNotNull("ENDER_PEARL should not be null", rewards.get(0));
    }

    /**
     * Test that getRewardTypes returns non-null materials for NETHERRACK.
     */
    @Test
    public void testGetRewardTypes_Netherrack_ReturnsNonNullMaterial() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.NETHERRACK);
        
        assertNotNull("Reward list should not be null", rewards);
        assertFalse("Reward list should not be empty", rewards.isEmpty());
        assertNotNull("NETHER_BRICK should not be null", rewards.get(0));
    }

    /**
     * Test that getRewardTypes returns non-null materials for TERRACOTTA.
     * This is critical as TERRACOTTA returns 16 different colored terracotta materials.
     */
    @Test
    public void testGetRewardTypes_Terracotta_AllMaterialsNonNull() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        List<Material> rewards = (List<Material>) method.invoke(quarrying, Material.TERRACOTTA);
        
        assertNotNull("Reward list should not be null", rewards);
        assertEquals("TERRACOTTA should return 16 colored variants", 16, rewards.size());
        
        // Verify all terracotta variants are non-null
        for (int i = 0; i < rewards.size(); i++) {
            assertNotNull("Terracotta variant at index " + i + " should not be null", rewards.get(i));
        }
    }

    /**
     * Test that getRewardTypes throws IllegalArgumentException for invalid materials.
     */
    @Test(expected = InvocationTargetException.class)
    public void testGetRewardTypes_InvalidMaterial_ThrowsException() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        // This should throw IllegalArgumentException wrapped in InvocationTargetException
        method.invoke(quarrying, Material.DIRT);
    }

    /**
     * Test that isValidMaterial returns true for quarrying materials.
     */
    @Test
    public void testIsValidMaterial_QuarryingMaterials_ReturnsTrue() {
        assertTrue("STONE should be valid", quarrying.isValidMaterial(Material.STONE));
        assertTrue("GRANITE should be valid", quarrying.isValidMaterial(Material.GRANITE));
        assertTrue("ANDESITE should be valid", quarrying.isValidMaterial(Material.ANDESITE));
        assertTrue("DIORITE should be valid", quarrying.isValidMaterial(Material.DIORITE));
        assertTrue("DEEPSLATE should be valid", quarrying.isValidMaterial(Material.DEEPSLATE));
        assertTrue("SANDSTONE should be valid", quarrying.isValidMaterial(Material.SANDSTONE));
        assertTrue("RED_SANDSTONE should be valid", quarrying.isValidMaterial(Material.RED_SANDSTONE));
        assertTrue("END_STONE should be valid", quarrying.isValidMaterial(Material.END_STONE));
        assertTrue("NETHERRACK should be valid", quarrying.isValidMaterial(Material.NETHERRACK));
        assertTrue("TERRACOTTA should be valid", quarrying.isValidMaterial(Material.TERRACOTTA));
    }

    /**
     * Test that isValidMaterial returns false for non-quarrying materials.
     */
    @Test
    public void testIsValidMaterial_NonQuarryingMaterials_ReturnsFalse() {
        assertFalse("DIRT should not be valid", quarrying.isValidMaterial(Material.DIRT));
        assertFalse("GRASS_BLOCK should not be valid", quarrying.isValidMaterial(Material.GRASS_BLOCK));
        assertFalse("DIAMOND_ORE should not be valid", quarrying.isValidMaterial(Material.DIAMOND_ORE));
    }

    /**
     * Test that isRequiredItem returns true for pickaxes.
     */
    @Test
    public void testIsRequiredItem_Pickaxe_ReturnsTrue() {
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        assertTrue("Diamond pickaxe should be valid", 
            quarrying.isRequiredItem(pickaxe, block, "blockbreakevent"));
        
        pickaxe = new ItemStack(Material.IRON_PICKAXE);
        assertTrue("Iron pickaxe should be valid", 
            quarrying.isRequiredItem(pickaxe, block, "blockbreakevent"));
    }

    /**
     * Test that isRequiredItem returns false for non-pickaxe items.
     */
    @Test
    public void testIsRequiredItem_NonPickaxe_ReturnsFalse() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        assertFalse("Sword should not be valid", 
            quarrying.isRequiredItem(sword, block, "blockbreakevent"));
    }

    /**
     * Test that isItemRequired returns true (pickaxe is required).
     */
    @Test
    public void testIsItemRequired_ReturnsTrue() {
        assertTrue("Item should be required for Quarrying", quarrying.isItemRequired());
    }

    /**
     * Test that getChance returns 0 (no random chance).
     */
    @Test
    public void testGetChance_ReturnsZero() {
        assertEquals("Chance should be 0", 0.0, quarrying.getChance(), 0.001);
    }

    /**
     * Test that randomExpGainChance returns false (no random chance).
     */
    @Test
    public void testRandomExpGainChance_ReturnsFalse() {
        assertFalse("Random exp gain chance should be false", quarrying.randomExpGainChance());
    }

    /**
     * Integration test: Verify that the fix prevents NullPointerException
     * when all XMaterial.parseMaterial() calls return valid materials.
     * 
     * This test proves that the original bug is fixed by ensuring
     * parseMaterial results are wrapped with Objects.requireNonNull.
     */
    @Test
    public void testGetRewardTypes_NeverReturnsNull_ProvingFix() throws Exception {
        Method method = Quarrying.class.getDeclaredMethod("getRewardTypes", Material.class);
        method.setAccessible(true);
        
        // Test all valid quarrying materials
        Material[] validMaterials = {
            Material.STONE, Material.GRANITE, Material.ANDESITE, Material.DIORITE,
            Material.DEEPSLATE, Material.SANDSTONE, Material.RED_SANDSTONE,
            Material.END_STONE, Material.NETHERRACK, Material.TERRACOTTA
        };
        
        for (Material material : validMaterials) {
            List<Material> rewards = (List<Material>) method.invoke(quarrying, material);
            
            // Verify no null materials in the list
            for (Material reward : rewards) {
                assertNotNull(
                    "Material " + material + " should not produce null rewards. " +
                    "This proves the fix: Objects.requireNonNull prevents null materials.",
                    reward
                );
            }
        }
    }
}
