package dansplugins.simpleskills.skill.skills;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.config.ConfigService;
import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.skill.abs.AbstractSkill;
import dansplugins.simpleskills.chance.ChanceCalculator;
import dansplugins.simpleskills.logging.Log;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 16:42
 */
public class MonsterHunting extends AbstractSkill {
    private final ChanceCalculator chanceCalculator;

    /**
     * The Monster Hunting skill is levelled by killing Monsters.
     */
    public MonsterHunting(ConfigService configService, Log log, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, ChanceCalculator chanceCalculator) {
        super(configService, log, playerRecordRepository, simpleSkills, messageService, "Monster Hunting", EntityDeathEvent.class);
        this.chanceCalculator = chanceCalculator;
    }

    /**
     * Method to get the chance of a skill incrementing or levelling.
     *
     * @return double chance (1-100).
     * @see #randomExpGainChance()
     */
    @Override
    public double getChance() {
        return 0;
    }

    /**
     * Method to determine if a skill is chance-incremented/levelled.
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean randomExpGainChance() {
        return false;
    }

    /**
     * Event handler for the Monster Hunting skill.
     *
     * @param event to handle.
     */
    @EventHandler
    public void onKill(@NotNull EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Monster)) return; // Only Monsters, zombies, creepers etc. etc.
        final Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        incrementExperience(killer);
        executeReward(killer, event.getEntity());
    }

    /**
     * Method to reward the player at their level.
     *
     * @param player    to reward.
     * @param skillData assigned data to the skill reward, 'Block' for 'BlockSkills' etc.
     */
    @Override
    public void executeReward(@NotNull Player player, Object... skillData) {
        final PlayerRecord record = getRecord(player);
        if (record == null) return;
        if (skillData.length != 1) throw new IllegalArgumentException("Skill Data is not of length '1'");
        final Object entityData = skillData[0];
        if (!(entityData instanceof Monster)) throw new IllegalArgumentException("Skill Data[0] is not Monster.");
        final Monster monster = (Monster) entityData;
        if (!chanceCalculator.roll(record, this, 0.10)) return;
        final List<LivingEntity> nearbyEntities = monster.getNearbyEntities(15, 5, 15).stream()
                .filter(e -> e.getType().equals(monster.getType()))
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(l -> !l.isDead())
                .collect(Collectors.toList());
        Location start = monster.getEyeLocation();
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 5, 2);
        if (start.getWorld() == null) return;
        if (nearbyEntities.size() == 0) return;
        for (LivingEntity nearbyEntity : nearbyEntities) {
            if (nearbyEntity == monster) continue;
            Vector vector = nearbyEntity.getEyeLocation().toVector().subtract(start.toVector());
            for (double i = 1; i <= start.distance(nearbyEntity.getEyeLocation()); i += 0.5) {
                vector.multiply(i);
                start.add(vector);
                player.spawnParticle(Particle.REDSTONE, start, 50,
                        new Particle.DustOptions(Color.fromRGB(128, 0, 128), 1.0f));
                player.playSound(nearbyEntity.getEyeLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 5, 2);
                start.subtract(vector);
                vector.normalize();
            }
        }
        final String mobName = WordUtils.capitalizeFully(monster.getType().name().toLowerCase().replaceAll("_", " "));
        final boolean sRequired = mobName.charAt(mobName.length() - 1) != 'd';
        player.sendMessage(messageService.convert(Objects.requireNonNull(Objects.requireNonNull(messageService.getlang().getString("Skills.MonsterHunting"))
                .replaceAll("%nearbyEntities%", String.valueOf(nearbyEntities.size()))
                .replaceAll("%mobName%", mobName + (sRequired ? "s" : "")))));
    }

}
