package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.inventory.ItemStack;
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
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes which breedings level {@link Breeding}, the guards in front of its benefit roll,
 * and the reward that follows a successful roll: the event's experience is doubled, a chime is
 * played, and a message names the bred animal.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class BreedingTest {

    private static final UUID BREEDER_ID = UUID.fromString("88888888-8888-8888-8888-888888888888");
    private static final String TEMPLATE = "double experience for breeding your %type% !";
    private static final int EXPERIENCE = 7;

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
    private World world;
    @Mock
    private Player breeder;
    @Mock
    private PlayerRecord breederRecord;
    @Mock
    private Cow child;
    @Mock
    private Cow mother;
    @Mock
    private PolarBear father;

    private Breeding breeding;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);
        when(messageService.getlang()).thenReturn(lang);
        when(lang.getString("Skills.Breeding")).thenReturn(TEMPLATE);
        when(messageService.convert(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        breeding = new Breeding(configService, log, playerRecordRepository, simpleSkills, messageService,
                chanceCalculator);

        when(breeder.getUniqueId()).thenReturn(BREEDER_ID);
        when(breeder.getLocation()).thenReturn(new Location(world, 5, 64, 5));
        when(playerRecordRepository.getPlayerRecord(BREEDER_ID)).thenReturn(breederRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);

        // The parents deliberately differ in type so that the message shows which one is named.
        when(mother.getType()).thenReturn(EntityType.COW);
        when(father.getType()).thenReturn(EntityType.POLAR_BEAR);
    }

    private EntityBreedEvent breedingBy(LivingEntity breedingEntity) {
        return new EntityBreedEvent(child, mother, father, breedingEntity, new ItemStack(Material.WHEAT),
                EXPERIENCE);
    }

    @Test
    public void handle_grantsTheBreederExperienceAndRollsTheBenefit_whenAPlayerBreedsAnimals() {
        breeding.handle(breedingBy(breeder));

        verify(breederRecord).incrementExperience(breeding.getId());
        verify(chanceCalculator).roll(eq(breederRecord), eq(breeding), eq(0.10));
    }

    @Test
    public void handle_ignoresTheBreeding_whenThereIsNoBreeder() {
        breeding.handle(breedingBy(null));

        verify(playerRecordRepository, never()).getPlayerRecord(any());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheBreeding_whenTheBreederIsNotAPlayer() {
        breeding.handle(breedingBy(mock(Fox.class)));

        verify(playerRecordRepository, never()).getPlayerRecord(any());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheBreeding_whenTheSkillIsInactive() {
        breeding.setActive(false);

        breeding.handle(breedingBy(breeder));

        verify(breederRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheBreeding_whenTheEventIsCancelled() {
        final EntityBreedEvent event = breedingBy(breeder);
        event.setCancelled(true);

        breeding.handle(event);

        verify(breederRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("breedingBenefitEnabled"), anyBoolean())).thenReturn(false);

        breeding.executeReward(breeder, breedingBy(breeder));

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(BREEDER_ID)).thenReturn(null);

        breeding.executeReward(breeder, breedingBy(breeder));

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_returnsQuietly_forSkillDataOfAnotherLength_whenThePlayerHasNoRecord() {
        // The record and benefit checks come before the skill data is checked, so a malformed
        // call is only rejected where a reward would have been attempted.
        when(playerRecordRepository.getPlayerRecord(BREEDER_ID)).thenReturn(null);

        breeding.executeReward(breeder);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        breeding.executeReward(breeder, breedingBy(breeder), breedingBy(breeder));
    }

    @Test
    public void executeReward_returnsQuietly_forSkillDataThatIsNotABreedEvent() {
        // Unlike a wrong length, a wrong type is not rejected with an exception.
        breeding.executeReward(breeder, "Cow");

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_leavesTheExperienceAndSendsNothing_whenTheRollFails() {
        final EntityBreedEvent event = breedingBy(breeder);

        breeding.executeReward(breeder, event);

        assertEquals(EXPERIENCE, event.getExperience());
        verify(breeder, never()).playSound(any(Location.class), any(Sound.class), anyFloat(), anyFloat());
        verify(breeder, never()).sendMessage(anyString());
    }

    @Test
    public void executeReward_doublesTheExperience_chimes_andNamesTheFathersTypeInThePlural() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(true);
        final EntityBreedEvent event = breedingBy(breeder);

        breeding.executeReward(breeder, event);

        assertEquals(EXPERIENCE * 2, event.getExperience());
        verify(breeder).playSound(any(Location.class), eq(Sound.BLOCK_AMETHYST_BLOCK_CHIME), eq(5f), eq(2f));
        verify(breeder).sendMessage("double experience for breeding your Polar Bears !");
    }
}
