package dansplugins.simpleskills.config;

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    @Test
    public void usageReportingKeyIsEmptyWhenAbsent() {
        when(pluginConfig.getString("usage-reporting.key")).thenReturn(null);

        assertEquals("", configService.getUsageReportingKey());
    }
}
