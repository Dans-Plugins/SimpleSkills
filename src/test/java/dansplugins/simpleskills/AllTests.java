package dansplugins.simpleskills;

import dansplugins.simpleskills.listeners.PlayerJoinEventListenerTest;
import dansplugins.simpleskills.listeners.WorldSaveEventListenerTest;
import dansplugins.simpleskills.services.StorageServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite that runs all unit tests for SimpleSkills plugin
 * 
 * This suite ensures comprehensive testing of:
 * - WorldSaveEventListener (prevents data loss during crashes)
 * - StorageService (persists player skill data)
 * - PlayerJoinEventListener (manages player records)
 * 
 * @author GitHub Copilot
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    WorldSaveEventListenerTest.class,
    StorageServiceTest.class,
    PlayerJoinEventListenerTest.class
})
public class AllTests {
    // This class remains empty, it is used only as a holder for the above annotations
}