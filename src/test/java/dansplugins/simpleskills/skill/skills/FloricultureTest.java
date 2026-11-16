package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
 * Characterizes which block breaks and placements level {@link Floriculture} and the guards in
 * front of its benefit roll.
 * <p>
 * Two paths are not exercised, because they reach {@code XMaterial}, which cannot initialise
 * without a running server: a right click on a flower pot (its item check compares the clicked
 * block with {@code XMaterial.FLOWER_POT}) and the reward after a successful roll (it drops
 * {@code XMaterial.BONE_MEAL}). The tests either keep the benefit roll failing or stop the event
 * before the item check.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class FloricultureTest {

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
    @Mock
    private World world;
    @Mock
    private Location location;

    private Floriculture floriculture;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        floriculture = new Floriculture(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getLocation()).thenReturn(location);
        holding(Material.AIR);
        when(block.getType()).thenReturn(Material.POPPY);
        when(block.hasMetadata(anyString())).thenReturn(false);
        when(block.getWorld()).thenReturn(world);
        when(block.getLocation()).thenReturn(location);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    private void holding(Material material) {
        when(inventory.getItemInMainHand()).thenReturn(new ItemStack(material));
    }

    private BlockBreakEvent breaking() {
        return new BlockBreakEvent(block, player);
    }

    private BlockPlaceEvent placing() {
        return new BlockPlaceEvent(block, replacedState, against, inventory.getItemInMainHand(), player, true,
                EquipmentSlot.HAND);
    }

    /**
     * Delivers the event the way the server does, through {@link Floriculture#handle(Event)}, which
     * applies the active and cancelled checks; calling a typed overload such as
     * {@code handle(BlockBreakEvent)} directly would skip both.
     */
    private void dispatch(Event event) {
        floriculture.handle(event);
    }

    private void verifyNothingGranted() {
        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_whenAFlowerIsBroken_whateverIsHeld() {
        for (Material item : new Material[]{Material.AIR, Material.IRON_SHOVEL, Material.DIRT}) {
            holding(item);

            dispatch(breaking());
        }

        verify(playerRecord, times(3)).incrementExperience(floriculture.getId());
        verify(chanceCalculator, times(3)).roll(eq(playerRecord), eq(floriculture), eq(0.10));
    }

    @Test
    public void handle_ignoresTheBreak_whenTheBlockIsNotAFlower() {
        when(block.getType()).thenReturn(Material.GRASS_BLOCK);

        dispatch(breaking());

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresTheBreak_whenTheFlowerWasPlacedByAPlayer() {
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
        floriculture.setActive(false);

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
    public void handle_grantsExperienceAndRollsTheBenefit_whenAFlowerIsPlantedFromTheMainHand() {
        // Placement has no player-placed guard, so this is pinned as it stands: see #263.
        holding(Material.POPPY);

        dispatch(placing());

        verify(playerRecord).incrementExperience(floriculture.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(floriculture), eq(0.10));
    }

    @Test
    public void handle_ignoresThePlacement_whenTheMainHandDoesNotHoldAFlower() {
        // The placed block is a flower, but the item check reads the main hand, which is empty.
        dispatch(placing());

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresThePlacement_whenThePlacedBlockIsNotAFlower() {
        holding(Material.POPPY);
        when(block.getType()).thenReturn(Material.DIRT);

        dispatch(placing());

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresAClick_withTheOffHand_orOnAir() {
        dispatch(new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, inventory.getItemInMainHand(), block,
                BlockFace.UP, EquipmentSlot.OFF_HAND));
        dispatch(new PlayerInteractEvent(player, Action.RIGHT_CLICK_AIR, inventory.getItemInMainHand(), null,
                BlockFace.UP, EquipmentSlot.HAND));

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresAClick_onABlockThatIsNotAFlowerOrPot() {
        when(block.getType()).thenReturn(Material.STONE);

        dispatch(new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, inventory.getItemInMainHand(), block,
                BlockFace.UP, EquipmentSlot.HAND));

        verifyNothingGranted();
    }

    @Test
    public void isRequiredItem_acceptsAnyItem_whenBreaking() {
        for (Material item : new Material[]{Material.AIR, Material.IRON_SHOVEL, Material.DIRT}) {
            assertTrue(item.name(), floriculture.isRequiredItem(new ItemStack(item), block, "blockbreakevent"));
        }
    }

    @Test
    public void isRequiredItem_acceptsOnlyAFlowerOrPot_whenPlacing() {
        assertTrue(floriculture.isRequiredItem(new ItemStack(Material.WITHER_ROSE), block, "blockplaceevent"));
        assertTrue(floriculture.isRequiredItem(new ItemStack(Material.FLOWER_POT), block, "blockplaceevent"));
        assertFalse(floriculture.isRequiredItem(new ItemStack(Material.DIRT), block, "blockplaceevent"));
        assertFalse(floriculture.isRequiredItem(new ItemStack(Material.AIR), block, "blockplaceevent"));
    }

    @Test
    public void isValidMaterial_acceptsTheListedFlowersAndTheFlowerPot() {
        for (Material material : new Material[]{Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID,
                Material.ALLIUM, Material.AZURE_BLUET, Material.ORANGE_TULIP, Material.PINK_TULIP,
                Material.RED_TULIP, Material.WHITE_TULIP, Material.OXEYE_DAISY, Material.SUNFLOWER,
                Material.LILAC, Material.ROSE_BUSH, Material.PEONY, Material.CORNFLOWER,
                Material.LILY_OF_THE_VALLEY, Material.WITHER_ROSE, Material.FLOWER_POT}) {
            assertTrue(material.name(), floriculture.isValidMaterial(material));
        }
    }

    @Test
    public void isValidMaterial_rejectsUnlistedBlocks_includingPottedFlowersAndTheSporeBlossom() {
        // A potted flower and the spore blossom are not in the list, and are pinned as they stand.
        for (Material material : new Material[]{Material.GRASS_BLOCK, Material.STONE, Material.OAK_SAPLING,
                Material.POTTED_POPPY, Material.SPORE_BLOSSOM}) {
            assertFalse(material.name(), floriculture.isValidMaterial(material));
        }
    }

    @Test
    public void isTriggeredByRightClickBreakOrPlace_withAnItemRequired_andWithoutRandomExperienceGain() {
        assertEquals(Floriculture.BlockSkillType.RIGHT_OR_BREAK_OR_PLACE, floriculture.getBlockSkillType());
        assertTrue(floriculture.isItemRequired());
        assertFalse(floriculture.randomExpGainChance());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("floricultureBenefitEnabled"), anyBoolean())).thenReturn(false);

        floriculture.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        floriculture.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_dropsAndSendsNothing_whenTheRollFails() {
        floriculture.executeReward(player, block, breaking());

        verify(chanceCalculator).roll(eq(playerRecord), eq(floriculture), eq(0.10));
        verify(player, never()).sendMessage(anyString());
        verify(block, never()).getWorld();
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        floriculture.executeReward(player, block);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsMalformedSkillData_evenWhenTheBenefitIsDisabled() {
        // The shape of the skill data is checked before the record and benefit checks.
        when(fileConfiguration.getBoolean(eq("floricultureBenefitEnabled"), anyBoolean())).thenReturn(false);

        floriculture.executeReward(player, "Poppy", breaking());
    }
}
