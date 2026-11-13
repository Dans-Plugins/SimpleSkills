package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.skill.abs.AbstractMovementSkill;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
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
 * Characterizes which moves level {@link Cardio} and the guards in front of its benefit roll.
 * <p>
 * The distance tally itself is covered by {@code AbstractMovementSkillTest}; these tests check
 * that Cardio is wired to the sprinting tally and dispatches through {@link Cardio#handle}.
 * Every move is one block along the X axis, so the sixth sprinting move is the rewarded one.
 * The reward itself (a Speed potion effect, a sound and a message) is not exercised: the
 * potion effect type is only registered by a running server, so every test here keeps the
 * benefit roll failing and treats reaching the roll as the sign that the reward was attempted.
 * </p>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class CardioTest {

    private static final UUID PLAYER_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

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
    private PlayerRecord playerRecord;
    @Mock
    private World world;

    private Cardio cardio;
    private double x;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);

        cardio = new Cardio(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(playerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);
    }

    @Test
    public void getSkillType_isSprinting() {
        assertEquals(AbstractMovementSkill.MovementSkillType.SPRINTING, cardio.getSkillType());
    }

    @Test
    public void handle_grantsExperienceAndRollsTheBenefit_onTheSixthSprintingMove() {
        when(player.isSprinting()).thenReturn(true);

        moveTimes(5);
        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());

        moveTimes(1);
        verify(playerRecord, times(1)).incrementExperience(cardio.getId());
        verify(chanceCalculator, times(1)).roll(eq(playerRecord), eq(cardio), eq(0.10));
    }

    @Test
    public void handle_ignoresSwimming_whichLevelsAthleteInstead() {
        when(player.isSwimming()).thenReturn(true);

        moveTimes(20);

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void handle_ignoresSprintingMoves_whenTheSkillIsInactive() {
        when(player.isSprinting()).thenReturn(true);
        cardio.setActive(false);

        moveTimes(20);

        verify(playerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresSprintingMoves_whenTheEventIsCancelled() {
        when(player.isSprinting()).thenReturn(true);

        for (int i = 0; i < 20; i++) {
            final PlayerMoveEvent event = nextMove();
            event.setCancelled(true);
            cardio.handle(event);
        }

        verify(playerRecord, never()).incrementExperience(anyInt());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("cardioBenefitEnabled"), anyBoolean())).thenReturn(false);

        cardio.executeReward(player);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(PLAYER_ID)).thenReturn(null);

        cardio.executeReward(player);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_doesNotTouchThePlayer_whenTheRollFails() {
        cardio.executeReward(player);

        verify(chanceCalculator).roll(eq(playerRecord), eq(cardio), eq(0.10));
        verify(player, never()).addPotionEffect(any());
        verify(player, never()).sendMessage(anyString());
    }

    /**
     * Sends {@link #player} one block along the X axis the given number of times.
     */
    private void moveTimes(int times) {
        for (int i = 0; i < times; i++) {
            cardio.handle(nextMove());
        }
    }

    private PlayerMoveEvent nextMove() {
        final Location from = new Location(world, x, 64, 0);
        x += 1;
        return new PlayerMoveEvent(player, from, new Location(world, x, 64, 0));
    }
}
