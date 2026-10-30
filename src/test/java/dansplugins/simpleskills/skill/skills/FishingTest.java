package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
 * Characterizes which fishing outcomes level {@link Fishing} and the guards in front of its
 * benefit roll.
 * <p>
 * The reward itself (a golden apple, a sound and a message) is not exercised: the apple's
 * material is resolved through XSeries, which expects a running server, so every test here keeps
 * the benefit roll failing and treats reaching the roll as the sign that the reward was attempted.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class FishingTest {

    private static final UUID PLAYER_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");

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
    private PlayerRecord record;
    @Mock
    private FishHook hook;
    @Mock
    private Item caughtItem;

    private Fishing fishing;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        fishing = new Fishing(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(record);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    private PlayerFishEvent fish(Entity caught, PlayerFishEvent.State state) {
        return new PlayerFishEvent(player, caught, hook, state);
    }

    private void dispatch(Event event) {
        fishing.handle(event);
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_whenAFishIsCaught() {
        dispatch(fish(caughtItem, PlayerFishEvent.State.CAUGHT_FISH));

        verify(record).incrementExperience(fishing.getId());
        verify(chanceCalculator).roll(eq(record), eq(fishing), eq(0.10));
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_whenAnEntityIsCaught() {
        // Any state whose name contains "CAUGHT" counts, so hooking a mob levels the skill too.
        dispatch(fish(caughtItem, PlayerFishEvent.State.CAUGHT_ENTITY));

        verify(record).incrementExperience(fishing.getId());
        verify(chanceCalculator).roll(eq(record), eq(fishing), eq(0.10));
    }

    @Test
    public void handle_ignoresTheEvent_whenNothingWasCaught() {
        dispatch(fish(null, PlayerFishEvent.State.FISHING));

        verify(record, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheEvent_whenTheStateIsACatchButNoEntityIsAttached() {
        dispatch(fish(null, PlayerFishEvent.State.CAUGHT_FISH));

        verify(record, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheEvent_whenTheStateIsNotACatch_evenWithAnEntityAttached() {
        dispatch(fish(caughtItem, PlayerFishEvent.State.REEL_IN));

        verify(record, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheEvent_whenTheSkillIsInactive() {
        fishing.setActive(false);

        dispatch(fish(caughtItem, PlayerFishEvent.State.CAUGHT_FISH));

        verify(record, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheEvent_whenItIsCancelled() {
        final PlayerFishEvent event = fish(caughtItem, PlayerFishEvent.State.CAUGHT_FISH);
        event.setCancelled(true);

        dispatch(event);

        verify(record, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_triesToCreateTheRecordAndSkipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);
        when(playerRecordRepository.createPlayerRecord(PLAYER_ID)).thenReturn(false);

        dispatch(fish(caughtItem, PlayerFishEvent.State.CAUGHT_FISH));

        verify(playerRecordRepository).createPlayerRecord(PLAYER_ID);
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("fishingBenefitEnabled"), anyBoolean())).thenReturn(false);

        fishing.executeReward(player, caughtItem);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        fishing.executeReward(player, caughtItem);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        fishing.executeReward(player, caughtItem, caughtItem);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataThatIsNotAnEntity() {
        fishing.executeReward(player, "Cod");
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataThatIsNotAnEntity_evenWhenThePlayerHasNoRecord() {
        // The length and type are checked before the record and benefit checks, so a malformed
        // call is rejected even where no reward would have been attempted.
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        fishing.executeReward(player, "Cod");
    }
}
