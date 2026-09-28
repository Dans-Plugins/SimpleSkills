package dansplugins.simpleskills.utils;

import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

/**
 * Constants that later Minecraft versions renamed, looked up by name so that one jar works on
 * every version listed in minecraft-versions.json. A direct reference such as
 * {@code Material.GRASS} names a field that only exists before the rename: newer servers
 * rewrite it when they load the plugin, but nothing guarantees they keep doing so, and the
 * build's API-compatibility check rejects it. Each lookup tries the current name first, then
 * the older one.
 */
public final class RenamedConstants {

    private RenamedConstants() {
    }

    /** The first of {@code names} this server knows as a potion effect type. */
    @SuppressWarnings("deprecation") // getByName is the one lookup every supported version has
    public static PotionEffectType potionEffect(String... names) {
        for (String name : names) {
            PotionEffectType type = PotionEffectType.getByName(name);
            if (type != null) {
                return type;
            }
        }
        throw new IllegalStateException("None of " + Arrays.toString(names) + " is a potion effect type on this server");
    }

    /** The first of {@code names} this server knows as a particle. */
    public static Particle particle(String... names) {
        for (String name : names) {
            try {
                return Particle.valueOf(name);
            } catch (IllegalArgumentException ignored) {
                // not on this version; try the next name
            }
        }
        throw new IllegalStateException("None of " + Arrays.toString(names) + " is a Particle on this server");
    }

    /** Strength: {@code STRENGTH} from 1.20.5, {@code INCREASE_DAMAGE} before. */
    public static PotionEffectType strength() {
        return potionEffect("STRENGTH", "INCREASE_DAMAGE");
    }

    /** Nausea: {@code NAUSEA} from 1.20.5, {@code CONFUSION} before. */
    public static PotionEffectType nausea() {
        return potionEffect("NAUSEA", "CONFUSION");
    }

    /** Coloured dust: {@code DUST} from 1.20.5, {@code REDSTONE} before. */
    public static Particle dust() {
        return particle("DUST", "REDSTONE");
    }
}
