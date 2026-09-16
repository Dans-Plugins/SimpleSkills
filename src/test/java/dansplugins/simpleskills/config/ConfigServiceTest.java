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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        when(pluginConfig.getBoolean("usage-reporting.enabled")).thenReturn(true);

        assertTrue(configService.isUsageReportingEnabled());

        verify(pluginConfig).getBoolean("usage-reporting.enabled");
        verify(pluginConfig, never()).getBoolean(eq("usage-reporting.enabled"), anyBoolean());
    }

    @Test
    public void usageReportingEnabledFalseIsHonoured() {
        when(pluginConfig.getBoolean("usage-reporting.enabled")).thenReturn(false);

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
}
