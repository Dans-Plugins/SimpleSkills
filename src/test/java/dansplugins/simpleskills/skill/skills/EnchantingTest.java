package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
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
 * Characterizes when {@link Enchanting} levels and how its reward rewrites the enchantments an
 * {@link EnchantItemEvent} is about to apply.
 * <p>
 * The reward bumps one offered enchantment by a level, unless it is already at the enchantment's
 * maximum, in which case it adds a different enchantment drawn from {@link Enchantment#values()}
 * that the item accepts and that neither is offered nor conflicts with an offered one. Without a
 * running server that registry is empty, so the reroll tests register their own enchantments once
 * for the whole JVM; each reroll test enchants a different item type, which keeps its candidates
 * apart from the other tests'.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class EnchantingTest {

    private static final UUID PLAYER_ID = UUID.fromString("88888888-8888-8888-8888-888888888888");
    private static final String TEMPLATE = "lucky %enchant% %level%";

    private static final Enchantment REROLL_TARGET =
            new StubEnchantment("characterization_reroll", 2, Material.DIAMOND_SWORD);
    private static final Enchantment REROLL_UNFIT = new StubEnchantment("characterization_unfit", 2);
    private static final Enchantment REROLL_SINGLE_LEVEL =
            new StubEnchantment("characterization_single_level", 1, Material.BOW);
    private static final Enchantment REROLL_OFFERED =
            new StubEnchantment("characterization_offered", 1, Material.TRIDENT);
    private static final Enchantment REROLL_CONFLICTING =
            new StubEnchantment("characterization_conflicting", 3, "characterization_offered", Material.TRIDENT);
    private static final Enchantment REROLL_FREE =
            new StubEnchantment("characterization_free", 3, Material.TRIDENT);

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
    private PlayerRecord playerRecord;
    @Mock
    private InventoryView view;
    @Mock
    private Block table;

    private Enchanting enchanting;

    @BeforeClass
    public static void registerRerollCandidates() {
        for (Enchantment enchantment : new Enchantment[]{REROLL_TARGET, REROLL_UNFIT, REROLL_SINGLE_LEVEL,
                REROLL_OFFERED, REROLL_CONFLICTING, REROLL_FREE}) {
            if (Enchantment.getByKey(enchantment.getKey()) == null) Enchantment.registerEnchantment(enchantment);
        }
    }

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);
        when(messageService.getlang()).thenReturn(lang);
        when(lang.getString("Skills.Enchanting")).thenReturn(TEMPLATE);
        when(messageService.convert(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        enchanting = new Enchanting(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    private EnchantItemEvent enchantOffering(Material item, Map<Enchantment, Integer> enchants) {
        return new EnchantItemEvent(player, view, table, new ItemStack(item), 30, new HashMap<>(enchants), 2);
    }

    private EnchantItemEvent enchantOffering(Map<Enchantment, Integer> enchants) {
        return enchantOffering(Material.DIAMOND_SWORD, enchants);
    }

    private EnchantItemEvent enchantOffering(Enchantment enchantment, int level) {
        final Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(enchantment, level);
        return enchantOffering(enchants);
    }

    /**
     * Delivers the event the way the server does, through {@link Enchanting#handle(Event)}, which
     * applies the active and cancelled checks; calling {@code onEnchant} directly would skip both.
     */
    private void dispatch(Event event) {
        enchanting.handle(event);
    }

    private void rollSucceeds() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(true);
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_forAnyEnchantment() {
        dispatch(enchantOffering(new StubEnchantment("sharpness", 5), 1));

        verify(playerRecord).incrementExperience(enchanting.getId());
        verify(chanceCalculator).roll(eq(playerRecord), eq(enchanting), eq(0.10));
    }

    @Test
    public void handle_ignoresTheEnchant_whenTheSkillIsInactive() {
        enchanting.setActive(false);

        dispatch(enchantOffering(new StubEnchantment("sharpness", 5), 1));

        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheEnchant_whenTheEventIsCancelled() {
        final EnchantItemEvent event = enchantOffering(new StubEnchantment("sharpness", 5), 1);
        event.setCancelled(true);

        dispatch(event);

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void executeReward_rejectsSkillDataThatIsNotASingleEnchantEvent() {
        final EnchantItemEvent event = enchantOffering(new StubEnchantment("sharpness", 5), 1);

        assertThrows(IllegalArgumentException.class, () -> enchanting.executeReward(player));
        assertThrows(IllegalArgumentException.class, () -> enchanting.executeReward(player, event, event));
        assertThrows(IllegalArgumentException.class, () -> enchanting.executeReward(player, "not an event"));
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("enchantingBenefitEnabled"), anyBoolean())).thenReturn(false);

        enchanting.executeReward(player, enchantOffering(new StubEnchantment("sharpness", 5), 1));

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        enchanting.executeReward(player, enchantOffering(new StubEnchantment("sharpness", 5), 1));

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_leavesTheEnchantmentsAlone_whenTheRollFails() {
        final Enchantment sharpness = new StubEnchantment("sharpness", 5);
        final EnchantItemEvent event = enchantOffering(sharpness, 1);

        enchanting.executeReward(player, event);

        assertEquals(Collections.singletonMap(sharpness, 1), event.getEnchantsToAdd());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void executeReward_raisesTheOfferedEnchantmentByOneLevel_andAnnouncesItInRomanNumerals() {
        rollSucceeds();
        final Enchantment sharpness = new StubEnchantment("sharpness", 5);
        final EnchantItemEvent event = enchantOffering(sharpness, 1);

        enchanting.executeReward(player, event);

        assertEquals(Collections.singletonMap(sharpness, 2), event.getEnchantsToAdd());
        verify(player).sendMessage("lucky Sharpness II");
    }

    @Test
    public void executeReward_titleCasesMultiWordKeys_andWritesFourAsIV() {
        rollSucceeds();
        final Enchantment fireAspect = new StubEnchantment("fire_aspect", 10);

        enchanting.executeReward(player, enchantOffering(fireAspect, 3));

        verify(player).sendMessage("lucky Fire Aspect IV");
    }

    @Test
    public void executeReward_writesNineAsIX() {
        rollSucceeds();

        enchanting.executeReward(player, enchantOffering(new StubEnchantment("power", 10), 8));

        verify(player).sendMessage("lucky Power IX");
    }

    @Test
    public void executeReward_raisesTheOfferedEnchantmentToItsMaximum_whenOneLevelBelowIt() {
        rollSucceeds();
        final Enchantment sharpness = new StubEnchantment("sharpness", 5);
        final EnchantItemEvent event = enchantOffering(sharpness, 4);

        enchanting.executeReward(player, event);

        assertEquals(Collections.singletonMap(sharpness, 5), event.getEnchantsToAdd());
        verify(player).sendMessage("lucky Sharpness V");
    }

    @Test
    public void executeReward_addsADifferentEnchantmentTheItemAccepts_atAnyOfItsLevels_whenAlreadyAtTheMaximum() {
        rollSucceeds();
        final Enchantment sharpness = new StubEnchantment("sharpness", 5);
        final Set<Integer> levelsSeen = new HashSet<>();

        for (int attempt = 0; attempt < 50; attempt++) {
            final EnchantItemEvent event = enchantOffering(sharpness, 5);

            enchanting.executeReward(player, event);

            assertEquals(Integer.valueOf(5), event.getEnchantsToAdd().get(sharpness));
            assertFalse(event.getEnchantsToAdd().containsKey(REROLL_UNFIT));
            levelsSeen.add(event.getEnchantsToAdd().get(REROLL_TARGET));
        }

        // REROLL_TARGET has a maximum of 2: both levels, the maximum included, can be drawn (#222).
        assertEquals(new HashSet<>(Arrays.asList(1, 2)), levelsSeen);
    }

    @Test
    public void executeReward_addsAMaxLevelOneEnchantmentAtLevelOne_whenAlreadyAtTheMaximum() {
        rollSucceeds();
        final Enchantment power = new StubEnchantment("power", 5);
        final EnchantItemEvent event = enchantOffering(Material.BOW, Collections.singletonMap(power, 5));

        enchanting.executeReward(player, event);

        assertEquals(Integer.valueOf(1), event.getEnchantsToAdd().get(REROLL_SINGLE_LEVEL));
        verify(player).sendMessage("lucky Characterization Single Level I");
    }

    @Test
    public void executeReward_neverRerollsIntoAnOfferedOrConflictingEnchantment() {
        rollSucceeds();

        for (int attempt = 0; attempt < 30; attempt++) {
            final EnchantItemEvent event = enchantOffering(Material.TRIDENT,
                    Collections.singletonMap(REROLL_OFFERED, 1));

            enchanting.executeReward(player, event);

            assertEquals(Integer.valueOf(1), event.getEnchantsToAdd().get(REROLL_OFFERED));
            assertFalse(event.getEnchantsToAdd().containsKey(REROLL_CONFLICTING));
            assertTrue(event.getEnchantsToAdd().containsKey(REROLL_FREE));
        }
    }

    @Test
    public void executeReward_leavesTheOfferAlone_whenAlreadyAtTheMaximumAndNoOtherEnchantmentFits() {
        rollSucceeds();
        final Enchantment efficiency = new StubEnchantment("efficiency", 5);
        final EnchantItemEvent event = enchantOffering(Material.SHEARS, Collections.singletonMap(efficiency, 5));

        enchanting.executeReward(player, event);

        assertEquals(Collections.singletonMap(efficiency, 5), event.getEnchantsToAdd());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void executeReward_doesNothing_whenNothingIsOffered() {
        rollSucceeds();
        final EnchantItemEvent event = enchantOffering(new HashMap<>());

        enchanting.executeReward(player, event);

        assertTrue(event.getEnchantsToAdd().isEmpty());
        verify(player, never()).sendMessage(anyString());
    }

    /**
     * A registry-free enchantment: the vanilla constants are wrappers that resolve through the
     * server's registry, which does not exist in a unit test.
     */
    private static final class StubEnchantment extends Enchantment {
        private final int maxLevel;
        private final String conflictKey;
        private final Set<Material> fits;

        StubEnchantment(String key, int maxLevel, Material... fits) {
            this(key, maxLevel, null, fits);
        }

        @SuppressWarnings("deprecation")
        StubEnchantment(String key, int maxLevel, String conflictKey, Material... fits) {
            super(NamespacedKey.minecraft(key));
            this.maxLevel = maxLevel;
            this.conflictKey = conflictKey;
            this.fits = new HashSet<>(Arrays.asList(fits));
        }

        @Override
        public String getName() {
            return getKey().getKey().toUpperCase();
        }

        @Override
        public int getMaxLevel() {
            return maxLevel;
        }

        @Override
        public int getStartLevel() {
            return 1;
        }

        @Override
        public EnchantmentTarget getItemTarget() {
            return EnchantmentTarget.ALL;
        }

        @Override
        public boolean isTreasure() {
            return false;
        }

        @Override
        public boolean isCursed() {
            return false;
        }

        @Override
        public boolean conflictsWith(Enchantment other) {
            return other.getKey().getKey().equals(conflictKey);
        }

        @Override
        public boolean canEnchantItem(ItemStack item) {
            return fits.contains(item.getType());
        }
    }
}
