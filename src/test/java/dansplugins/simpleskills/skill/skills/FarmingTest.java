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
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes which block breaks and placements level {@link Farming} and the guards in front
 * of its benefit roll.
 * <p>
 * Two paths are not exercised. Placing a block with a crop item in hand checks the block below
 * against {@code XMaterial.FARMLAND}, and the reward compares against the same constant; XSeries
 * resolves materials against a running server, so every placement here is one that is rejected
 * before that check, and every test keeps the benefit roll failing and treats reaching the roll
 * as the sign that the reward was attempted.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class FarmingTest {

    private static final UUID PLAYER_ID = UUID.fromString("88888888-8888-8888-8888-888888888888");

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

    private Farming farming;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        farming = new Farming(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getInventory()).thenReturn(inventory);
        holding(Material.IRON_HOE);
        when(block.getType()).thenReturn(Material.WHEAT);
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

    private BlockPlaceEvent placement() {
        return new BlockPlaceEvent(block, replacedState, against, inventory.getItemInMainHand(), player, true,
                EquipmentSlot.HAND);
    }

    /**
     * Delivers the event the way the server does, through {@link Farming#handle(Event)}, which
     * applies the active and cancelled checks; calling a typed overload such as
     * {@code handle(BlockBreakEvent)} directly would skip both.
     */
    private void dispatch(Event event) {
        farming.handle(event);
    }

    private void verifyNothingGranted() {
        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_whenACropIsBroken() {
        dispatch(breaking());

        verify(playerRecord).incrementExperience(farming.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(farming), eq(0.10));
    }

    @Test
    public void handle_grantsExperience_forABreakWithAnyItemInHand() {
        // A break always satisfies isRequiredItem, whatever the main hand holds.
        holding(Material.AIR);

        dispatch(breaking());

        verify(playerRecord).incrementExperience(farming.getId());
    }

    @Test
    public void handle_grantsExperience_forBreakingFarmland() {
        when(block.getType()).thenReturn(Material.FARMLAND);

        dispatch(breaking());

        verify(playerRecord).incrementExperience(farming.getId());
    }

    @Test
    public void handle_ignoresTheBreak_whenTheBlockIsNotAFarmingMaterial() {
        when(block.getType()).thenReturn(Material.STONE);

        dispatch(breaking());

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresTheBreak_ofCropBlocksWhoseNameDiffersFromTheirItem() {
        // The material list names some crops by their item (BEETROOT, COCOA_BEANS, GLOW_BERRIES)
        // rather than by the block that is actually broken, so these crops never level the skill.
        for (Material material : new Material[]{Material.BEETROOTS, Material.COCOA, Material.CAVE_VINES,
                Material.CAVE_VINES_PLANT, Material.NETHER_WART, Material.MELON, Material.PUMPKIN}) {
            when(block.getType()).thenReturn(material);

            dispatch(breaking());
        }

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
        farming.setActive(false);

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
    public void handle_ignoresThePlacement_whenTheMainHandDoesNotHoldAFarmingItem() {
        holding(Material.STONE);

        dispatch(placement());

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresThePlacement_ofCropBlocksWhoseNameDiffersFromTheirItem() {
        // The placed block's material is checked before the item in hand, so planting beetroot,
        // melon, pumpkin or cocoa (which place BEETROOTS, MELON_STEM, PUMPKIN_STEM and COCOA)
        // never levels the skill, even though the seeds themselves are on the material list.
        final Material[][] plantings = {
                {Material.BEETROOT_SEEDS, Material.BEETROOTS},
                {Material.MELON_SEEDS, Material.MELON_STEM},
                {Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM},
                {Material.COCOA_BEANS, Material.COCOA},
        };
        for (Material[] planting : plantings) {
            holding(planting[0]);
            when(block.getType()).thenReturn(planting[1]);

            dispatch(placement());
        }

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresThePlacement_whenThePlayerIsNotInSurvival() {
        holding(Material.WHEAT_SEEDS);
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);

        dispatch(placement());

        verifyNothingGranted();
    }

    @Test
    public void isRequiredItem_acceptsAnyItem_whenTheContextIsABreak() {
        assertTrue(farming.isRequiredItem(new ItemStack(Material.STONE), block, "blockbreakevent"));
        assertTrue(farming.isRequiredItem(new ItemStack(Material.AIR), block, "blockbreakevent"));
    }

    @Test
    public void isRequiredItem_rejectsAnItemThatIsNotAFarmingMaterial_outsideABreak() {
        assertFalse(farming.isRequiredItem(new ItemStack(Material.STONE), block, "blockplaceevent"));
        assertFalse(farming.isRequiredItem(new ItemStack(Material.IRON_HOE), block, "playerinteractevent"));
    }

    @Test
    public void isValidMaterial_acceptsTheListedCropsSeedsAndFarmland() {
        for (Material material : new Material[]{Material.WHEAT, Material.WHEAT_SEEDS, Material.BEETROOT,
                Material.BEETROOT_SEEDS, Material.CARROT, Material.CARROTS, Material.POTATO, Material.POTATOES,
                Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Material.COCOA_BEANS, Material.SUGAR_CANE,
                Material.CHORUS_FLOWER, Material.CHORUS_PLANT, Material.CHORUS_FRUIT, Material.BAMBOO,
                Material.SWEET_BERRY_BUSH, Material.CACTUS, Material.SEA_PICKLE, Material.BROWN_MUSHROOM,
                Material.RED_MUSHROOM, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS, Material.GLOW_BERRIES,
                Material.FARMLAND, Material.KELP}) {
            assertTrue(material.name(), farming.isValidMaterial(material));
        }
    }

    @Test
    public void isValidMaterial_rejectsUnlistedMaterials() {
        for (Material material : new Material[]{Material.STONE, Material.DIRT, Material.IRON_HOE,
                Material.KELP_PLANT, Material.MELON, Material.PUMPKIN}) {
            assertFalse(material.name(), farming.isValidMaterial(material));
        }
    }

    @Test
    public void isTriggeredByBreakingOrPlacing_withAnItemRequired_andWithoutRandomExperienceGain() {
        assertEquals(Farming.BlockSkillType.BREAK_OR_PLACE, farming.getBlockSkillType());
        assertTrue(farming.isItemRequired());
        assertFalse(farming.randomExpGainChance());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("farmingBenefitEnabled"), anyBoolean())).thenReturn(false);

        farming.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        farming.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_sendsNothing_whenTheRollFails() {
        farming.executeReward(player, block, breaking());

        verify(chanceCalculator).roll(eq(playerRecord), eq(farming), eq(0.10));
        verify(player, never()).sendMessage(anyString());
        verify(block, never()).getDrops(any(), any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        farming.executeReward(player, block);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataWhoseFirstElementIsNotABlock() {
        farming.executeReward(player, "Wheat", breaking());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataWhoseSecondElementIsNotAnEvent() {
        farming.executeReward(player, block, "break");
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsMalformedSkillData_evenWhenTheBenefitIsDisabled() {
        // The shape of the skill data is checked before the record and benefit checks.
        when(fileConfiguration.getBoolean(eq("farmingBenefitEnabled"), anyBoolean())).thenReturn(false);

        farming.executeReward(player, block, "break");
    }
}
