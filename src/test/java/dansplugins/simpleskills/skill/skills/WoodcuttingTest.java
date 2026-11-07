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
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes which block breaks level {@link Woodcutting}, the guards in front of its benefit
 * roll, and the reward after a successful roll.
 * <p>
 * The reward picks one of its two benefits at random and the choice cannot be fixed from a test,
 * so the reward test repeats it often enough to see both and asserts what each one does.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class WoodcuttingTest {

    private static final UUID PLAYER_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final int REWARD_REPETITIONS = 200;

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
    private FileConfiguration lang;
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
    @Mock
    private ExperienceOrb orb;

    private Woodcutting woodcutting;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        woodcutting = new Woodcutting(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getLocation()).thenReturn(location);
        holding(Material.IRON_AXE);
        when(block.getType()).thenReturn(Material.DARK_OAK_LOG);
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

    /**
     * Delivers the event the way the server does, through {@link Woodcutting#handle(Event)}, which
     * applies the active and cancelled checks; calling a typed overload such as
     * {@code handle(BlockBreakEvent)} directly would skip both.
     */
    private void dispatch(Event event) {
        woodcutting.handle(event);
    }

    private void verifyNothingGranted() {
        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_whenALogIsBrokenWithAnAxe() {
        dispatch(breaking());

        verify(playerRecord).incrementExperience(woodcutting.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(woodcutting), eq(0.10));
    }

    @Test
    public void handle_grantsExperience_whenTheMainHandIsEmpty() {
        holding(Material.AIR);

        dispatch(breaking());

        verify(playerRecord).incrementExperience(woodcutting.getId());
    }

    @Test
    public void handle_ignoresTheBreak_whenTheMainHandHoldsAnotherItem() {
        for (Material item : new Material[]{Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.OAK_LOG}) {
            holding(item);

            dispatch(breaking());
        }

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresTheBreak_whenTheBlockIsNotWood() {
        when(block.getType()).thenReturn(Material.OAK_PLANKS);

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
        woodcutting.setActive(false);

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
    public void handle_ignoresPlacingALog() {
        dispatch(new BlockPlaceEvent(block, replacedState, against, inventory.getItemInMainHand(), player, true,
                EquipmentSlot.HAND));

        verifyNothingGranted();
    }

    @Test
    public void handle_ignoresClickingALog() {
        // A right click with an axe is how a log is stripped; that does not count.
        dispatch(new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, inventory.getItemInMainHand(), block,
                BlockFace.UP, EquipmentSlot.HAND));

        verifyNothingGranted();
    }

    @Test
    public void isRequiredItem_acceptsAnAxeOrAnEmptyHand_whateverTheBlock() {
        for (Material item : new Material[]{Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
                Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE, Material.AIR}) {
            assertTrue(item.name(), woodcutting.isRequiredItem(new ItemStack(item), block, "blockbreakevent"));
        }
    }

    @Test
    public void isRequiredItem_doesNotMistakeAPickaxeForAnAxe() {
        // Only "_AXE" is matched, so the "AXE" at the end of a pickaxe's name does not count.
        assertFalse(woodcutting.isRequiredItem(new ItemStack(Material.IRON_PICKAXE), block, "blockbreakevent"));
    }

    @Test
    public void isValidMaterial_acceptsLogsAndWood_includingStrippedForms() {
        for (Material material : new Material[]{Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
                Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.OAK_WOOD,
                Material.STRIPPED_OAK_LOG, Material.STRIPPED_DARK_OAK_WOOD}) {
            assertTrue(material.name(), woodcutting.isValidMaterial(material));
        }
    }

    @Test
    public void isValidMaterial_rejectsOtherBlocks() {
        // Nether stems and hyphae are pinned as they stand: see #248.
        for (Material material : new Material[]{Material.OAK_PLANKS, Material.OAK_LEAVES, Material.STONE,
                Material.CRIMSON_STEM, Material.WARPED_STEM, Material.STRIPPED_CRIMSON_STEM,
                Material.CRIMSON_HYPHAE, Material.WARPED_HYPHAE}) {
            assertFalse(material.name(), woodcutting.isValidMaterial(material));
        }
    }

    @Test
    public void isTriggeredByBreakingOnly_withAnItemRequired_andWithoutRandomExperienceGain() {
        assertEquals(Woodcutting.BlockSkillType.BREAK_SPECIFIC, woodcutting.getBlockSkillType());
        assertTrue(woodcutting.isItemRequired());
        assertFalse(woodcutting.randomExpGainChance());
    }

    @Test
    public void isNamedLumberjack_ratherThanWoodcutting() {
        assertEquals("Lumberjack", woodcutting.getName());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled_underItsLumberjackKey() {
        when(fileConfiguration.getBoolean(eq("lumberjackBenefitEnabled"), anyBoolean())).thenReturn(false);

        woodcutting.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        woodcutting.executeReward(player, block, breaking());

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_sendsNothing_whenTheRollFails() {
        woodcutting.executeReward(player, block, breaking());

        verify(chanceCalculator).roll(eq(playerRecord), eq(woodcutting), eq(0.10));
        verify(player, never()).sendMessage(anyString());
        verify(block, never()).getWorld();
    }

    @Test
    public void executeReward_doesNotInspectTheSecondElementOfTheSkillData() {
        woodcutting.executeReward(player, block, "break");

        verify(chanceCalculator).roll(eq(playerRecord), eq(woodcutting), eq(0.10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        woodcutting.executeReward(player, block);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsMalformedSkillData_evenWhenTheBenefitIsDisabled() {
        // The shape of the skill data is checked before the record and benefit checks.
        when(fileConfiguration.getBoolean(eq("lumberjackBenefitEnabled"), anyBoolean())).thenReturn(false);

        woodcutting.executeReward(player, "Oak Log", breaking());
    }

    @Test
    public void executeReward_chimesAndGivesEitherDoubleDropsOrExperience_whenTheRollSucceeds() {
        final ItemStack drop = new ItemStack(Material.DARK_OAK_LOG);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(true);
        when(block.getDrops(any(ItemStack.class))).thenReturn(Arrays.asList(drop, new ItemStack(Material.AIR)));
        when(world.spawnEntity(location, EntityType.EXPERIENCE_ORB)).thenReturn(orb);
        when(messageService.getlang()).thenReturn(lang);
        when(messageService.convert(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lang.getString("Skills.WoodCutting.DoubleDrop")).thenReturn("double %item%");
        when(lang.getString("Skills.WoodCutting.Exp")).thenReturn("exp %exp%");

        for (int i = 0; i < REWARD_REPETITIONS; i++) {
            woodcutting.executeReward(player, block, breaking());
        }

        verify(player, times(REWARD_REPETITIONS)).playSound(eq(location), eq(Sound.BLOCK_AMETHYST_BLOCK_CHIME),
                anyFloat(), anyFloat());

        final ArgumentCaptor<String> messages = ArgumentCaptor.forClass(String.class);
        verify(player, times(REWARD_REPETITIONS)).sendMessage(messages.capture());
        final Set<String> distinctMessages = new HashSet<>(messages.getAllValues());
        assertTrue("A run should reach double drops", distinctMessages.remove("double Dark Oak Log"));
        assertFalse("A run should reach experience", distinctMessages.isEmpty());
        for (String message : distinctMessages) {
            assertTrue(message, message.matches("exp [1-5]"));
        }

        // Each double-drop message drops the block's drops once, skipping the air among them.
        final int doubleDrops = (int) messages.getAllValues().stream().filter("double Dark Oak Log"::equals).count();
        verify(world, times(doubleDrops)).dropItemNaturally(location, drop);
        verify(world, never()).dropItemNaturally(location, new ItemStack(Material.AIR));

        final ArgumentCaptor<Integer> experience = ArgumentCaptor.forClass(Integer.class);
        verify(orb, atLeastOnce()).setExperience(experience.capture());
        final List<Integer> amounts = experience.getAllValues();
        assertEquals(REWARD_REPETITIONS - doubleDrops, amounts.size());
        for (int amount : amounts) {
            assertTrue(String.valueOf(amount), amount >= 1 && amount <= 5);
        }
        verify(orb, times(amounts.size())).setGlowing(true);
    }
}
