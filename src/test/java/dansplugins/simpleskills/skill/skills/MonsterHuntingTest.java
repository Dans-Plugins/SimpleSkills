package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.logging.Log;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Characterizes which deaths level {@link MonsterHunting}, the guards in front of its benefit
 * roll, and the reward that follows a successful roll: a chime, a particle trail to each other
 * living monster of the same type nearby, and a message counting them.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class MonsterHuntingTest {

    private static final UUID KILLER_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private static final String TEMPLATE = "tracked down %nearbyEntities% other %mobName%";

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
    private Player killer;
    @Mock
    private PlayerRecord killerRecord;
    @Mock
    private Zombie monster;

    private MonsterHunting monsterHunting;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(fileConfiguration);
        when(fileConfiguration.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(fileConfiguration.getInt(anyString(), anyInt())).thenReturn(10);
        when(fileConfiguration.getDouble(anyString(), anyDouble())).thenReturn(1.2);
        when(messageService.getlang()).thenReturn(lang);
        when(lang.getString("Skills.MonsterHunting")).thenReturn(TEMPLATE);
        when(messageService.convert(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        monsterHunting = new MonsterHunting(configService, log, playerRecordRepository, simpleSkills,
                messageService, chanceCalculator);

        when(killer.getUniqueId()).thenReturn(KILLER_ID);
        when(killer.getLocation()).thenReturn(new Location(world, 5, 64, 5));
        when(playerRecordRepository.getPlayerRecord(KILLER_ID)).thenReturn(killerRecord);
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(false);

        when(monster.getType()).thenReturn(EntityType.ZOMBIE);
        when(monster.getEyeLocation()).thenReturn(new Location(world, 0, 65, 0));
        when(monster.getNearbyEntities(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Collections.<Entity>emptyList());
    }

    private EntityDeathEvent deathOf(org.bukkit.entity.LivingEntity entity) {
        return new EntityDeathEvent(entity, new ArrayList<ItemStack>());
    }

    private Zombie livingZombieAt(double x) {
        final Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);
        when(zombie.isDead()).thenReturn(false);
        when(zombie.getEyeLocation()).thenReturn(new Location(world, x, 65, 0));
        return zombie;
    }

    @Test
    public void handle_grantsTheKillerExperienceAndRollsTheBenefit_whenAMonsterIsKilledByAPlayer() {
        when(monster.getKiller()).thenReturn(killer);

        monsterHunting.handle(deathOf(monster));

        verify(killerRecord).incrementExperience(monsterHunting.getId());
        verify(chanceCalculator).roll(eq(killerRecord), eq(monsterHunting), eq(0.10));
    }

    @Test
    public void handle_ignoresTheDeath_whenTheEntityIsNotAMonster() {
        final Cow cow = mock(Cow.class);
        when(cow.getKiller()).thenReturn(killer);

        monsterHunting.handle(deathOf(cow));

        verify(killerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheDeath_whenThereIsNoKillingPlayer() {
        when(monster.getKiller()).thenReturn(null);

        monsterHunting.handle(deathOf(monster));

        verify(playerRecordRepository, never()).getPlayerRecord(any());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void handle_ignoresTheDeath_whenTheSkillIsInactive() {
        when(monster.getKiller()).thenReturn(killer);
        monsterHunting.setActive(false);

        monsterHunting.handle(deathOf(monster));

        verify(killerRecord, never()).incrementExperience(anyInt());
        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenTheBenefitIsDisabled() {
        when(fileConfiguration.getBoolean(eq("monsterHuntingBenefitEnabled"), anyBoolean())).thenReturn(false);

        monsterHunting.executeReward(killer, monster);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_skipsTheRoll_whenThePlayerHasNoRecord() {
        when(playerRecordRepository.getPlayerRecord(KILLER_ID)).thenReturn(null);

        monsterHunting.executeReward(killer, monster);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test
    public void executeReward_returnsQuietly_forSkillDataOfAnotherLength_whenThePlayerHasNoRecord() {
        // Unlike Dueling, the record and benefit checks come before the skill data is checked,
        // so a malformed call is only rejected where a reward would have been attempted.
        when(playerRecordRepository.getPlayerRecord(KILLER_ID)).thenReturn(null);

        monsterHunting.executeReward(killer);

        verify(chanceCalculator, never()).roll(any(PlayerRecord.class), any(), anyDouble());
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataOfAnotherLength() {
        monsterHunting.executeReward(killer, monster, monster);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeReward_rejectsSkillDataThatIsNotAMonster() {
        monsterHunting.executeReward(killer, "Zombie");
    }

    @Test
    public void executeReward_doesNothingVisible_whenTheRollFails() {
        monsterHunting.executeReward(killer, monster);

        verify(monster, never()).getNearbyEntities(anyDouble(), anyDouble(), anyDouble());
        verify(killer, never()).playSound(any(Location.class), any(Sound.class), anyFloat(), anyFloat());
        verify(killer, never()).sendMessage(anyString());
    }

    @Test
    public void executeReward_playsTheChimeButSendsNoMessage_whenNoOtherMonsterOfTheTypeIsNearby() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(true);

        monsterHunting.executeReward(killer, monster);

        verify(monster).getNearbyEntities(15, 5, 15);
        verify(killer).playSound(any(Location.class), eq(Sound.BLOCK_AMETHYST_BLOCK_CHIME), eq(5f), eq(2f));
        verify(killer, never()).sendMessage(anyString());
    }

    @Test
    public void executeReward_countsOnlyLivingMonstersOfTheSameType_andPluralizesTheMobName() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(true);
        final Zombie deadZombie = livingZombieAt(3);
        when(deadZombie.isDead()).thenReturn(true);
        final Skeleton skeleton = mock(Skeleton.class);
        when(skeleton.getType()).thenReturn(EntityType.SKELETON);
        final List<Entity> nearby = Arrays.<Entity>asList(livingZombieAt(2), livingZombieAt(4), deadZombie, skeleton);
        when(monster.getNearbyEntities(anyDouble(), anyDouble(), anyDouble())).thenReturn(nearby);

        monsterHunting.executeReward(killer, monster);

        verify(killer).sendMessage("tracked down 2 other Zombies");
        verify(killer, atLeastOnce()).spawnParticle(any(Particle.class), any(Location.class), eq(50),
                any(Particle.DustOptions.class));
        verify(killer, atLeastOnce()).playSound(any(Location.class), eq(Sound.ENTITY_ENDER_EYE_DEATH), eq(5f), eq(2f));
    }

    @Test
    public void executeReward_playsTheChimeButSendsNoMessage_whenTheMonsterHasNoWorld() {
        when(chanceCalculator.roll(any(PlayerRecord.class), any(), anyDouble())).thenReturn(true);
        when(monster.getEyeLocation()).thenReturn(new Location(null, 0, 65, 0));
        final Zombie nearbyZombie = livingZombieAt(2);
        when(monster.getNearbyEntities(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Collections.<Entity>singletonList(nearbyZombie));

        monsterHunting.executeReward(killer, monster);

        verify(killer).playSound(any(Location.class), eq(Sound.BLOCK_AMETHYST_BLOCK_CHIME), eq(5f), eq(2f));
        verify(killer, never()).sendMessage(anyString());
    }
}
