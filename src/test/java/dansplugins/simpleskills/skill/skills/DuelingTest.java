package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.UUID;

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
 * Characterizes which deaths level {@link Dueling} and the guards in front of its benefit roll.
 * <p>
 * The reward itself (an Absorption potion effect, two sounds and a message) is not exercised:
 * the potion effect type is only registered by a running server, so every test here keeps the
 * benefit roll failing and treats reaching the roll as the sign that the reward was attempted.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class DuelingTest {

    private static final UUID KILLER_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID VICTIM_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

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
    private Player killer;
    @Mock
    private Player victim;
    @Mock
    private PlayerRecord killerRecord;
    @Mock
    private PlayerRecord victimRecord;

    private Dueling dueling;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        dueling = new Dueling(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(killer.getUniqueId()).thenReturn(KILLER_ID);
        when(victim.getUniqueId()).thenReturn(VICTIM_ID);
        when(victim.getName()).thenReturn("Victim");
        when(playerRecordRepository.getPlayerRecord(KILLER_ID)).thenReturn(killerRecord);
        when(playerRecordRepository.getPlayerRecord(VICTIM_ID)).thenReturn(victimRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    private PlayerDeathEvent deathOf(Player player) {
        return new PlayerDeathEvent(player, new ArrayList<ItemStack>(), 0, "died");
    }

    @Test
    public void handle_grantsTheKillerExperienceAndRollsTheBenefit_whenAPlayerIsKilledByAPlayer() {
        when(victim.getKiller()).thenReturn(killer);

        dueling.handle(deathOf(victim));

        verify(killerRecord).incrementExperience(dueling.getId());
        verify(chanceCalculator).roll(eq(killerRecord), eq(dueling), eq(0.10));
    }

    @Test
    public void handle_leavesTheVictimsExperienceAlone_whenAPlayerIsKilledByAPlayer() {
        when(victim.getKiller()).thenReturn(killer);

        dueling.handle(deathOf(victim));

        verify(victimRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void handle_ignoresTheDeath_whenThereIsNoKillingPlayer() {
        when(victim.getKiller()).thenReturn(null);

        dueling.handle(deathOf(victim));

        verify(playerRecordRepository, never()).getPlayerRecord(any());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheDeath_whenTheSkillIsInactive() {
        when(victim.getKiller()).thenReturn(killer);
        dueling.setActive(false);

        dueling.handle(deathOf(victim));

        verify(killerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_triesToCreateTheKillersRecordAndSkipsTheRoll_whenTheKillerHasNoRecord() {
        when(victim.getKiller()).thenReturn(killer);
        when(playerRecordRepository.getPlayerRecord(KILLER_ID)).thenReturn(null);
        when(playerRecordRepository.createPlayerRecord(KILLER_ID)).thenReturn(false);

        dueling.handle(deathOf(victim));

        verify(playerRecordRepository).createPlayerRecord(KILLER_ID);
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("duelingBenefitEnabled"), anyBoolean())).thenReturn(false);

        dueling.executeReward(killer, "Victim");

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(KILLER_ID)).thenReturn(null);

        dueling.executeReward(killer, "Victim");

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_rollsTheBenefit_whateverTheTypeOfTheSkillData() {
        // Unlike Strength, the single piece of skill data is only ever turned into a string for
        // the reward message, so it is not checked to be of any particular type.
        dueling.executeReward(killer, 42);

        verify(chanceCalculator).roll(eq(killerRecord), eq(dueling), eq(0.10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        dueling.executeReward(killer, "Victim", "Bystander");
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength_evenWhenThePlayerHasNoRecord() {
        // Unlike Strength, the length is checked before the record and benefit checks, so a
        // malformed call is rejected even where no reward would have been attempted.
        when(playerRecordRepository.getPlayerRecord(KILLER_ID)).thenReturn(null);

        dueling.executeReward(killer);
    }
}
