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
 * Characterizes which block breaks level {@link Digging} and the guards in front of its benefit
 * roll.
 * <p>
 * The reward after a successful roll is not exercised: one of its three randomly chosen benefits
 * resolves its drops through {@code XMaterial.parseMaterial()}, which needs a running server, and
 * the choice cannot be fixed from a test. Every test keeps the roll failing and treats reaching it
 * as the sign that the reward was attempted.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class DiggingTest {

    private static final UUID PLAYER_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

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

    private Digging digging;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        digging = new Digging(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getInventory()).thenReturn(inventory);
        holding(Material.IRON_SHOVEL);
        when(block.getType()).thenReturn(Material.DIRT);
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
     * Delivers the event the way the server does, through {@link Digging#handle(Event)}, which
     * applies the active and cancelled checks; calling a typed overload such as
     * {@code handle(BlockBreakEvent)} directly would skip both.
     */
    private void dispatch(Event event) {
        digging.handle(event);
    }

    private void verifyNothingGranted() {
        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_whenDirtIsBrokenWithAShovel() {
        dispatch(breaking());

        verify(playerRecord).incrementExperience(digging.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(digging), eq(0.10));
    }

    @Test
    public void handle_grantsExperience_forEveryShovel() {
        final Material[] shovels = {Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
                Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL};
        for (Material shovel : shovels) {
            holding(shovel);

            dispatch(breaking());
        }

        verify(playerRecord, times(shovels.length)).incrementExperience(digging.getId());
    }

    @Test
    public void handle_grantsExperience_whenDirtIsBrokenWithAnEmptyHand() {
        // isRequiredItem also accepts air, so digging by hand counts although the skill requires an item.
        holding(Material.AIR);

        dispatch(breaking());

        verify(playerRecord).incrementExperience(digging.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(digging), eq(0.10));
    }

    @Test
    public void handle_ignoresTheBreak_whenTheMainHandHoldsSomethingOtherThanAShovel() {
        for (Material item : new Material[]{Material.IRON_PICKAXE, Material.IRON_AXE, Material.DIRT}) {
            holding(item);

            dispatch(breaking());
        }

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresTheBreak_whenTheBlockIsNotDiggable() {
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
        digging.setActive(false);

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
    public void handle_ignoresPlacingDirt() {
        dispatch(new BlockPlaceEvent(block, replacedState, against, inventory.getItemInMainHand(), player, true,
                EquipmentSlot.HAND));

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresClickingDirt() {
        dispatch(new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, inventory.getItemInMainHand(), block,
                BlockFace.UP, EquipmentSlot.HAND));

        verifyNothingGranted();
    }

    @Test
    public void isRequiredItem_acceptsAShovelOrAnEmptyHand_whateverTheBlock() {
        assertTrue(digging.isRequiredItem(new ItemStack(Material.DIAMOND_SHOVEL), block, "blockbreakevent"));
        assertTrue(digging.isRequiredItem(new ItemStack(Material.AIR), block, "blockbreakevent"));
        assertFalse(digging.isRequiredItem(new ItemStack(Material.DIAMOND_PICKAXE), block, "blockbreakevent"));
        assertFalse(digging.isRequiredItem(new ItemStack(Material.DIAMOND_HOE), block, "blockbreakevent"));
    }

    @Test
    public void isValidMaterial_acceptsTheElevenListedBlocks() {
        for (Material material : new Material[]{Material.GRASS_BLOCK, Material.DIRT, Material.GRAVEL,
                Material.SAND, Material.RED_SAND, Material.SOUL_SAND, Material.COARSE_DIRT,
                Material.ROOTED_DIRT, Material.PODZOL, Material.MYCELIUM, Material.MOSS_BLOCK}) {
            assertTrue(material.name(), digging.isValidMaterial(material));
        }
    }

    @Test
    public void isValidMaterial_rejectsOtherShovelBlocks_becauseTheListIsExact() {
        for (Material material : new Material[]{Material.CLAY, Material.SOUL_SOIL, Material.FARMLAND,
                Material.DIRT_PATH, Material.SNOW_BLOCK, Material.SNOW, Material.SANDSTONE, Material.STONE}) {
            assertFalse(material.name(), digging.isValidMaterial(material));
        }
    }

    @Test
    public void isTriggeredByBreakingOnly_withAnItemRequired_andWithoutRandomExperienceGain() {
        assertEquals(Digging.BlockSkillType.BREAK_SPECIFIC, digging.getBlockSkillType());
        assertTrue(digging.isItemRequired());
        assertFalse(digging.randomExpGainChance());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("diggingBenefitEnabled"), anyBoolean())).thenReturn(false);

        digging.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        digging.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_sendsNothing_whenTheRollFails() {
        digging.executeReward(player, block, breaking());

        verify(chanceCalculator).roll(eq(playerRecord), eq(digging), eq(0.10));
        verify(player, never()).sendMessage(anyString());
        verify(block, never()).getWorld();
    }

    @Test
    public void executeReward_doesNotInspectTheSecondElementOfTheSkillData() {
        digging.executeReward(player, block, "break");

        verify(chanceCalculator).roll(eq(playerRecord), eq(digging), eq(0.10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        digging.executeReward(player, block);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsMalformedSkillData_evenWhenTheBenefitIsDisabled() {
        // The shape of the skill data is checked before the record and benefit checks.
        when(fileConfiguration.getBoolean(eq("diggingBenefitEnabled"), anyBoolean())).thenReturn(false);

        digging.executeReward(player, "Dirt", breaking());
    }
}
