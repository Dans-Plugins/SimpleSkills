package dansplugins.simpleskills.utils;

import org.bukkit.Particle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RenamedConstantsTest {

    @Test
    public void dustIsTheOldNameBeforeTheRename() {
        // the build's spigot-api predates the rename, so only REDSTONE exists here
        assertEquals(Particle.valueOf("REDSTONE"), RenamedConstants.dust());
    }

    @Test
    public void particleTakesTheFirstNameThisServerHas() {
        assertEquals(Particle.FLAME, RenamedConstants.particle("NOT_A_PARTICLE", "FLAME"));
    }

    @Test(expected = IllegalStateException.class)
    public void particleRefusesWhenNoNameExists() {
        RenamedConstants.particle("NOT_A_PARTICLE");
    }

    // Potion effect types are only registered by a running server, so strength() and
    // nausea() are verified by the API-compatibility check (getByName exists on every
    // supported version) rather than here.
}
