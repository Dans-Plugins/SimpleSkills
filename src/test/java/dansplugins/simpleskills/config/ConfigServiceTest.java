package dansplugins.simpleskills.config;

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pins how {@link ConfigService} reads the usage-reporting settings.
 * <p>
 * The settings must come from the plugin's own {@code getConfig()} -- the one Bukkit registers
 * the jar's {@code config.yml} as defaults for -- and through the one-argument getters, so that
 * an installation whose on-disk {@code config.yml} predates the {@code usage-reporting} block
 * still picks up the bundled key rather than an explicit fallback that would turn reporting off.
 * </p>
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceTest {
    @Mock
    private SimpleSkills simpleSkills;

    @Mock
    private FileConfiguration pluginConfig;

    @Rule
    public TemporaryFolder dataFolder = new TemporaryFolder();

    private ConfigService configService;

    @Before
    public void setUp() {
        when(simpleSkills.getConfig()).thenReturn(pluginConfig);
        configService = new ConfigService(simpleSkills);
    }

    @Test
    public void usageReportingEnabledIsReadWithTheOneArgumentGetter() {
        when(pluginConfig.get("usage-reporting.enabled")).thenReturn(true);

        assertTrue(configService.isUsageReportingEnabled());

        verify(pluginConfig).get("usage-reporting.enabled");
        verify(pluginConfig, never()).get(eq("usage-reporting.enabled"), any());
        verify(pluginConfig, never()).getBoolean(eq("usage-reporting.enabled"), anyBoolean());
    }

    @Test
    public void usageReportingEnabledFalseIsHonoured() {
        when(pluginConfig.get("usage-reporting.enabled")).thenReturn(false);

        assertFalse(configService.isUsageReportingEnabled());
    }

    @Test
    public void usageReportingEndpointIsReadWithTheOneArgumentGetter() {
        when(pluginConfig.getString("usage-reporting.endpoint")).thenReturn("http://localhost:1234");

        assertEquals("http://localhost:1234", configService.getUsageReportingEndpoint());

        verify(pluginConfig).getString("usage-reporting.endpoint");
        verify(pluginConfig, never()).getString(eq("usage-reporting.endpoint"), anyString());
    }

    @Test
    public void usageReportingEndpointFallsBackToTheTraceServerWhenAbsent() {
        when(pluginConfig.getString("usage-reporting.endpoint")).thenReturn(null);

        assertEquals("https://trace.danielstephenson.dev", configService.getUsageReportingEndpoint());
    }

    @Test
    public void usageReportingKeyIsReadWithTheOneArgumentGetter() {
        when(pluginConfig.getString("usage-reporting.key")).thenReturn("bundled-key");

        assertEquals("bundled-key", configService.getUsageReportingKey());

        verify(pluginConfig).getString("usage-reporting.key");
        verify(pluginConfig, never()).getString(eq("usage-reporting.key"), anyString());
    }

    /** A data folder holding the given config.yml, loaded the way onEnable() loads it. */
    private File loadFromDisk(String onDisk) throws IOException {
        File file = new File(dataFolder.getRoot(), "config.yml");
        Files.write(file.toPath(), onDisk.getBytes(StandardCharsets.UTF_8));
        // createConfig() itself needs the plugin's final getDataFolder()/getLogger(), which a
        // mock cannot provide, so the file is handed over at the point where it is known.
        configService.load(file);
        return file;
    }

    private YamlConfiguration bundledDefaults() {
        YamlConfiguration bundled = new YamlConfiguration();
        bundled.set("usage-reporting.enabled", true);
        bundled.set("usage-reporting.endpoint", "https://trace.danielstephenson.dev");
        bundled.set("usage-reporting.key", "bundled-key");
        return bundled;
    }

    @Test
    public void missingUsageReportingBlockIsWrittenToDiskFromTheBundledDefaults() throws IOException {
        // The in-place upgrade case: the file predates the block, the getters fall through to
        // the bundled key, and the switch the console line points at does not exist on disk.
        File file = loadFromDisk("version: 2.0.0\nskills:\n  Mining:\n    active: true\n");
        when(pluginConfig.getDefaults()).thenReturn(bundledDefaults());

        configService.saveUsageReportingDefaultsIfNotPresent();

        YamlConfiguration onDisk = YamlConfiguration.loadConfiguration(file);
        assertTrue(onDisk.getBoolean("usage-reporting.enabled"));
        assertEquals("https://trace.danielstephenson.dev", onDisk.getString("usage-reporting.endpoint"));
        assertEquals("bundled-key", onDisk.getString("usage-reporting.key"));
        assertTrue(onDisk.getBoolean("skills.Mining.active"));
        // and it is in the configuration saveConfig() writes back on disable, so it survives.
        assertEquals("bundled-key", configService.getConfig().getString("usage-reporting.key"));
    }

    @Test
    public void existingUsageReportingBlockIsLeftAlone() throws IOException {
        File file = loadFromDisk("usage-reporting:\n  enabled: false\n  endpoint: http://localhost:8080\n  key: abc\n");
        long before = file.lastModified();

        configService.saveUsageReportingDefaultsIfNotPresent();

        YamlConfiguration onDisk = YamlConfiguration.loadConfiguration(file);
        assertFalse(onDisk.getBoolean("usage-reporting.enabled"));
        assertEquals("abc", onDisk.getString("usage-reporting.key"));
        assertEquals(before, file.lastModified());
    }

    @Test
    public void usageReportingBlockIsNotInventedWithoutBundledDefaults() throws IOException {
        File file = loadFromDisk("version: 2.0.0\n");
        when(pluginConfig.getDefaults()).thenReturn(null);

        configService.saveUsageReportingDefaultsIfNotPresent();

        assertFalse(YamlConfiguration.loadConfiguration(file).isSet("usage-reporting"));
    }

    @Test
    public void usageReportingKeyIsEmptyWhenAbsent() {
        when(pluginConfig.getString("usage-reporting.key")).thenReturn(null);

        assertEquals("", configService.getUsageReportingKey());
    }

    // An operator who turns usage reporting off must find it off, every time.

    @Test
    public void aQuotedFalseTurnsUsageReportingOff() {
        // getBoolean() would return the bundled default, true, for the string "false".
        when(pluginConfig.get("usage-reporting.enabled")).thenReturn("false");

        assertFalse(configService.isUsageReportingEnabled());
    }

    @Test
    public void anOptOutOnDiskWinsOverThePluginsLoadedConfig() throws IOException {
        // Bukkit's getConfig() still says true: it was loaded before the operator's edit. (Lenient:
        // the opt-out on disk decides before it is consulted.)
        lenient().when(pluginConfig.get("usage-reporting.enabled")).thenReturn(true);
        loadFromDisk("usage-reporting:\n  enabled: false\n");

        assertFalse(configService.isUsageReportingEnabled());
    }

    @Test
    public void aSkillToggleKeepsAnOptOutWrittenByHandWhileRunning() throws IOException {
        File file = loadFromDisk("usage-reporting:\n  enabled: true\nskills:\n  Mining:\n    active: true\n");
        // The operator turns reporting off by hand while the server runs.
        Files.write(file.toPath(), "usage-reporting:\n  enabled: false\nskills:\n  Mining:\n    active: true\n"
                .getBytes(StandardCharsets.UTF_8));

        // /ss force writes this service's (stale) configuration back.
        configService.setSkillActive("Mining", false);

        YamlConfiguration onDisk = YamlConfiguration.loadConfiguration(file);
        assertEquals(Boolean.FALSE, onDisk.get("usage-reporting.enabled"));
        assertFalse(onDisk.getBoolean("skills.Mining.active"));
    }

    @Test
    public void aConfigFileThatCannotBeParsedIsNeverOverwrittenAndReportingIsOff() throws IOException {
        // load() logs and keeps an empty configuration, so the upgrade step would find no block,
        // copy in the bundled enabled: true and write it over the operator's whole file. (load()
        // logs through the plugin's final getLogger(), which a mock cannot provide, so "loaded as
        // empty" is set up by loading an empty file and then breaking it.)
        String broken = "usage-reporting:\n  enabled: false\n\tskills: [\n";
        File file = loadFromDisk("");
        Files.write(file.toPath(), broken.getBytes(StandardCharsets.UTF_8));
        when(pluginConfig.getDefaults()).thenReturn(bundledDefaults());
        lenient().when(pluginConfig.get("usage-reporting.enabled")).thenReturn(true);

        configService.saveUsageReportingDefaultsIfNotPresent();
        configService.setSkillActive("Mining", false);

        assertEquals(broken, new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
        assertFalse(configService.isUsageReportingEnabled());
    }

    @Test
    public void anOptOutWrittenUnderANearMissNameTurnsUsageReportingOff() throws IOException {
        // usage_reporting is not the block the plugin reads, so its own getConfig() still says true.
        lenient().when(pluginConfig.get("usage-reporting.enabled")).thenReturn(true);
        loadFromDisk("usage_reporting:\n  enabled: false\n");

        assertFalse(configService.isUsageReportingEnabled());
    }

    @Test
    public void addingTheBlockOverANearMissOptOutWritesTheRealSwitchOff() throws IOException {
        // The file has no usage-reporting block, only the operator's usage_reporting one, so the
        // upgrade step copies in the bundled block, enabled: true; the save must write it off.
        File file = loadFromDisk("version: 2.0.0\nusage_reporting:\n  enabled: false\n");
        when(pluginConfig.getDefaults()).thenReturn(bundledDefaults());

        configService.saveUsageReportingDefaultsIfNotPresent();

        YamlConfiguration onDisk = YamlConfiguration.loadConfiguration(file);
        assertEquals(Boolean.FALSE, onDisk.get("usage-reporting.enabled"));
        assertEquals("bundled-key", onDisk.getString("usage-reporting.key"));
    }
}
