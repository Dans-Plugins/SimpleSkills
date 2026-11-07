package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes which block breaks level {@link Mining} and the guards in front of its benefit
 * roll.
 * <p>
 * The reward after a successful roll is not exercised: one of its three randomly chosen benefits
 * reads the server's recipes through {@code Bukkit.recipeIterator()}, which needs a running
 * server, and the choice cannot be fixed from a test. Every test keeps the roll failing and
 * treats reaching it as the sign that the reward was attempted.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class MiningTest {

    private static final UUID PLAYER_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

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
    private FileConfiguration fileConfiguration;
    @Mock
    private Player player;
    @Mock
    private PlayerInventory inventory;
    @Mock
    private PlayerRecord playerRecord;
    @Mock
    private Block block;
    @Mock
    private Block against;
    @Mock
    private BlockState replacedState;

    private Mining mining;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        mining = new Mining(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getInventory()).thenReturn(inventory);
        holding(Material.IRON_PICKAXE);
        when(block.getType()).thenReturn(Material.IRON_ORE);
        when(block.hasMetadata(anyString())).thenReturn(false);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    private void holding(Material material) {
        when(inventory.getItemInMainHand()).thenReturn(new ItemStack(material));
    }

    private BlockBreakEvent breaking() {
        return new BlockBreakEvent(block, player);
    }

    /**
     * Delivers the event the way the server does, through {@link Mining#handle(Event)}, which
     * applies the active and cancelled checks; calling a typed overload such as
     * {@code handle(BlockBreakEvent)} directly would skip both.
     */
    private void dispatch(Event event) {
        mining.handle(event);
    }

    private void verifyNothingGranted() {
        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_whenAnOreIsBrokenWithAPickaxe() {
        dispatch(breaking());

        verify(playerRecord).incrementExperience(mining.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(mining), eq(0.10));
    }

    @Test
    public void handle_grantsExperience_forEveryPickaxe() {
        final Material[] pickaxes = {Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
                Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE};
        for (Material pickaxe : pickaxes) {
            holding(pickaxe);

            dispatch(breaking());
        }

        verify(playerRecord, times(pickaxes.length)).incrementExperience(mining.getId());
    }

    @Test
    public void handle_ignoresTheBreak_whenTheMainHandDoesNotHoldAPickaxe() {
        for (Material item : new Material[]{Material.AIR, Material.IRON_SHOVEL, Material.IRON_AXE}) {
            holding(item);

            dispatch(breaking());
        }

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresTheBreak_whenTheBlockIsNotAnOre() {
        when(block.getType()).thenReturn(Material.STONE);

        dispatch(breaking());

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresTheBreak_whenTheBlockWasPlacedByAPlayer() {
        when(block.hasMetadata("simpleskills-player-placed")).thenReturn(true);

        dispatch(breaking());

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresTheBreak_whenThePlayerIsNotInSurvival() {
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);

        dispatch(breaking());

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresTheBreak_whenTheSkillIsInactive() {
        mining.setActive(false);

        dispatch(breaking());

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresTheBreak_whenTheEventIsCancelled() {
        final BlockBreakEvent event = breaking();
        event.setCancelled(true);

        dispatch(event);

        verifyNothingGranted();
    }

    @Test
    public void handle_triesToCreateTheRecordAndSkipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);
        when(playerRecordRepository.createPlayerRecord(PLAYER_ID)).thenReturn(false);

        dispatch(breaking());

        verify(playerRecordRepository).createPlayerRecord(PLAYER_ID);
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresPlacingAnOre() {
        dispatch(new BlockPlaceEvent(block, replacedState, against, inventory.getItemInMainHand(), player, true,
                EquipmentSlot.HAND));

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresClickingAnOre() {
        dispatch(new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, inventory.getItemInMainHand(), block,
                BlockFace.UP, EquipmentSlot.HAND));

        verifyNothingGranted();
    }

    @Test
    public void isRequiredItem_acceptsOnlyAPickaxe_whateverTheBlock() {
        assertTrue(mining.isRequiredItem(new ItemStack(Material.DIAMOND_PICKAXE), block, "blockbreakevent"));
        assertFalse(mining.isRequiredItem(new ItemStack(Material.DIAMOND_SHOVEL), block, "blockbreakevent"));
        assertFalse(mining.isRequiredItem(new ItemStack(Material.AIR), block, "blockbreakevent"));
    }

    @Test
    public void isValidMaterial_acceptsOres() {
        for (Material material : new Material[]{Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, Material.IRON_ORE,
                Material.COPPER_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE, Material.LAPIS_ORE,
                Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
                Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE}) {
            assertTrue(material.name(), mining.isValidMaterial(material));
        }
    }

    @Test
    public void isValidMaterial_rejectsOtherBlocks() {
        for (Material material : new Material[]{Material.STONE, Material.ANCIENT_DEBRIS,
                Material.RAW_IRON_BLOCK, Material.IRON_BLOCK}) {
            assertFalse(material.name(), mining.isValidMaterial(material));
        }
    }

    @Test
    public void isValidMaterial_acceptsASporeBlossom_becauseItsNameContainsOre() {
        // Pinned as it stands, not as intended: the material name is matched by substring (#247).
        assertTrue(mining.isValidMaterial(Material.SPORE_BLOSSOM));
    }

    @Test
    public void isTriggeredByBreakingOnly_withAnItemRequired_andWithoutRandomExperienceGain() {
        assertEquals(Mining.BlockSkillType.BREAK_SPECIFIC, mining.getBlockSkillType());
        assertTrue(mining.isItemRequired());
        assertFalse(mining.randomExpGainChance());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("miningBenefitEnabled"), anyBoolean())).thenReturn(false);

        mining.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        mining.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_sendsNothing_whenTheRollFails() {
        mining.executeReward(player, block, breaking());

        verify(chanceCalculator).roll(eq(playerRecord), eq(mining), eq(0.10));
        verify(player, never()).sendMessage(anyString());
        verify(block, never()).getWorld();
    }

    @Test
    public void executeReward_doesNotInspectTheSecondElementOfTheSkillData() {
        mining.executeReward(player, block, "break");

        verify(chanceCalculator).roll(eq(playerRecord), eq(mining), eq(0.10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        mining.executeReward(player, block);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsMalformedSkillData_evenWhenTheBenefitIsDisabled() {
        // The shape of the skill data is checked before the record and benefit checks.
        when(fileConfiguration.getBoolean(eq("miningBenefitEnabled"), anyBoolean())).thenReturn(false);

        mining.executeReward(player, "Iron Ore", breaking());
    }
}
