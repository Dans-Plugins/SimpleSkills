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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes which placements level {@link Pyromaniac} and the guards in front of its
 * benefit roll.
 * <p>
 * The reward itself (fire resistance, speed, weakness and nausea, a sound and a message) is not
 * exercised: the potion effect types are only registered by a running server, so every test
 * here keeps the benefit roll failing and treats reaching the roll as the sign that the reward
 * was attempted.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PyromaniacTest {

    private static final UUID PLAYER_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");

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

    private Pyromaniac pyromaniac;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        pyromaniac = new Pyromaniac(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getInventory()).thenReturn(inventory);
        holding(Material.FLINT_AND_STEEL);
        when(block.getType()).thenReturn(Material.FIRE);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    private void holding(Material material) {
        when(inventory.getItemInMainHand()).thenReturn(new ItemStack(material));
    }

    private BlockPlaceEvent placement() {
        return new BlockPlaceEvent(block, replacedState, against, inventory.getItemInMainHand(), player, true,
                EquipmentSlot.HAND);
    }

    /**
     * Delivers the event the way the server does, through {@link Pyromaniac#handle(Event)}, which
     * applies the active and cancelled checks; calling a typed overload such as
     * {@code handle(BlockPlaceEvent)} directly would skip both.
     */
    private void dispatch(Event event) {
        pyromaniac.handle(event);
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_whenFireIsPlacedWithFlintAndSteel() {
        dispatch(placement());

        verify(playerRecord).incrementExperience(pyromaniac.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(pyromaniac), eq(0.10));
    }

    @Test
    public void handle_grantsExperience_whenFireIsPlacedWithAFireCharge() {
        holding(Material.FIRE_CHARGE);

        dispatch(placement());

        verify(playerRecord).incrementExperience(pyromaniac.getId());
    }

    @Test
    public void handle_grantsExperience_forAPlacedBlockOfAnyMaterial_whileHoldingFlintAndSteel() {
        // isValidMaterial accepts every material; only the item in the main hand is checked.
        when(block.getType()).thenReturn(Material.STONE);

        dispatch(placement());

        verify(playerRecord).incrementExperience(pyromaniac.getId());
    }

    @Test
    public void handle_ignoresThePlacement_whenTheMainHandHoldsAnotherItem() {
        holding(Material.TORCH);

        dispatch(placement());

        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresThePlacement_whenThePlayerIsNotInSurvival() {
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);

        dispatch(placement());

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void handle_ignoresThePlacement_whenTheSkillIsInactive() {
        pyromaniac.setActive(false);

        dispatch(placement());

        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresThePlacement_whenTheEventIsCancelled() {
        final BlockPlaceEvent event = placement();
        event.setCancelled(true);

        dispatch(event);

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void handle_ignoresABlockBreak_evenWhileHoldingFlintAndSteel() {
        dispatch(new BlockBreakEvent(block, player));

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void handle_ignoresAClick_evenWithFlintAndSteelOnABlock() {
        final ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL);

        dispatch(new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, flintAndSteel, block,
                BlockFace.UP, EquipmentSlot.HAND));
        dispatch(new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, flintAndSteel, block,
                BlockFace.UP, EquipmentSlot.HAND));

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void isRequiredItem_acceptsOnlyFlintAndSteelAndFireCharges() {
        assertTrue(pyromaniac.isRequiredItem(new ItemStack(Material.FLINT_AND_STEEL), block, "blockplaceevent"));
        assertTrue(pyromaniac.isRequiredItem(new ItemStack(Material.FIRE_CHARGE), block, "blockplaceevent"));
        assertFalse(pyromaniac.isRequiredItem(new ItemStack(Material.LAVA_BUCKET), block, "blockplaceevent"));
        assertFalse(pyromaniac.isRequiredItem(new ItemStack(Material.TORCH), block, "blockplaceevent"));
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("pyromaniacBenefitEnabled"), anyBoolean())).thenReturn(false);

        pyromaniac.executeReward(player, block);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        pyromaniac.executeReward(player, block);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_rollsRegardlessOfTheSkillData() {
        // Unlike the entity skills, the skill data is never inspected.
        pyromaniac.executeReward(player);
        pyromaniac.executeReward(player, "not a block", mock(Block.class), 3);

        verify(chanceCalculator, times(2)).roll(eq(playerRecord), eq(pyromaniac), eq(0.10));
    }

    @Test
    public void executeReward_sendsNothing_whenTheRollFails() {
        pyromaniac.executeReward(player, block);

        verify(player, never()).sendMessage(anyString());
        verify(player, never()).addPotionEffect(any());
    }
}
