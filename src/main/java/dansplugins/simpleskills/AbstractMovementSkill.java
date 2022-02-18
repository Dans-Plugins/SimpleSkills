package dansplugins.simpleskills;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Callum Johnson
 * @since 11/01/2022 - 23:09
 */
public abstract class AbstractMovementSkill extends AbstractSkill {

    /**
     * Map to save the distance travelled whilst moving.
     */
    private final HashMap<UUID, Double> move = new HashMap<>();

    /**
     * Map to save the distance travelled whilst gliding.
     */
    private final HashMap<UUID, Double> glide = new HashMap<>();

    /**
     * Map to save the distance travelled whilst swimming.
     */
    private final HashMap<UUID, Double> swim = new HashMap<>();

    /**
     * Map to save the distance travelled whilst riding.
     */
    private final HashMap<UUID, Double> ride = new HashMap<>();

    /**
     * Map to save the distance travelled whilst boating.
     */
    private final HashMap<UUID, Double> boat = new HashMap<>();

    /**
     * The overhead Movement Skill abstraction class.
     * <p>
     * This class is an extension of the {@link AbstractSkill} class, providing
     * specific functionality for moving around the world and linking it to experience gain for Skills.
     * </p>
     */
    public AbstractMovementSkill(@NotNull String name) {
        super(name, PlayerMoveEvent.class);
    }

    /**
     * Event handler to handle movement.
     *
     * @param event to handle.
     */
    public void onMove(@NotNull PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
        final Location to = event.getTo();
        if (to == null) return;
        final Location from = event.getFrom();
        if (matches(from, to)) return;
        final UUID id = event.getPlayer().getUniqueId();
        final Player player = event.getPlayer();
        if (getSkillType() == MovementSkillType.SPRINTING && player.isSprinting() && !player.isSwimming()
                && !player.isFlying() && !player.isGliding() && !player.isInsideVehicle()) {
            if (!move.containsKey(id)) {
                move.put(id, from.distanceSquared(to));
                return;
            } else {
                if (move.getOrDefault(id, 0.0) <= 4.0) {
                    move.put(id, move.getOrDefault(id, 0.0) + from.distanceSquared(to));
                    return;
                } else move.remove(id);
            }
            incrementExperience(event.getPlayer());
            executeReward(player, event);
        } else if (getSkillType() == MovementSkillType.GLIDING && player.isGliding()) {
            if (!glide.containsKey(id)) {
                glide.put(id, from.distanceSquared(to));
                return;
            } else {
                if (glide.getOrDefault(id, 0.0) <= 16.0) {
                    glide.put(id, glide.getOrDefault(id, 0.0) + from.distanceSquared(to));
                    return;
                } else glide.remove(id);
            }
            incrementExperience(event.getPlayer());
            executeReward(player, event);
        } else if (getSkillType() == MovementSkillType.SWIMMING && player.isSwimming() &&
                !player.isFlying() && !player.isGliding()) {
            if (!swim.containsKey(id)) {
                swim.put(id, from.distanceSquared(to));
                return;
            } else {
                if (swim.getOrDefault(id, 0.0) <= 1.0) {
                    swim.put(id, swim.getOrDefault(id, 0.0) + from.distanceSquared(to));
                    return;
                } else swim.remove(id);
            }
            incrementExperience(event.getPlayer());
            executeReward(player, event);
        } else if (getSkillType() == MovementSkillType.RIDING && player.isInsideVehicle()
                && player.getVehicle() instanceof Creature) {
            if (!ride.containsKey(id)) {
                ride.put(id, from.distanceSquared(to));
                return;
            } else {
                if (ride.getOrDefault(id, 0.0) <= 8.0) {
                    ride.put(id, ride.getOrDefault(id, 0.0) + from.distanceSquared(to));
                    return;
                } else ride.remove(id);
            }
            incrementExperience(event.getPlayer());
            executeReward(player, event);
        } else if (getSkillType() == MovementSkillType.BOATING && player.isInsideVehicle()
                && player.getVehicle() instanceof Boat) {
            if (!boat.containsKey(id)) {
                boat.put(id, from.distanceSquared(to));
                return;
            } else {
                if (boat.getOrDefault(id, 0.0) <= 8.0) {
                    boat.put(id, boat.getOrDefault(id, 0.0) + from.distanceSquared(to));
                    return;
                } else boat.remove(id);
            }
            incrementExperience(event.getPlayer());
            executeReward(player, event);
        }
    }

    /**
     * Helper method to determine if "from" matches "to".
     *
     * @param from location
     * @param to   location
     * @return {@code true} if it does.
     */
    private boolean matches(Location from, Location to) {
        int match = 0;
        final double movementConstant = 0.25;
        if (Math.max(from.getX(), to.getX()) - Math.min(from.getX(), to.getX()) < movementConstant) match++;
        else match--;
        if (Math.max(from.getY(), to.getY()) - Math.min(from.getY(), to.getY()) < movementConstant) match++;
        else match--;
        if (Math.max(from.getZ(), to.getZ()) - Math.min(from.getZ(), to.getZ()) < movementConstant) match++;
        else match--;
        return match >= 2;
    }

    /**
     * Method to obtain the skill type for the skill.
     *
     * @return {@link MovementSkillType}
     */
    public abstract MovementSkillType getSkillType();

    public enum MovementSkillType {

        /**
         * Sprinting
         */
        SPRINTING,

        /**
         * Swimming
         */
        SWIMMING,

        /**
         * Gliding
         */
        GLIDING,

        /**
         * Riding an Entity
         */
        RIDING,

        /**
         * Swimming but this time... in a boat.
         */
        BOATING

    }

}
