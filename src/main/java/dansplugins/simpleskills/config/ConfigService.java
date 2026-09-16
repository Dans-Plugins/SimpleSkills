package dansplugins.simpleskills.config;

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Daniel Stephenson
 */
public class ConfigService {
    private static final String USAGE_REPORTING_SECTION = "usage-reporting";
    private static final String USAGE_REPORTING_ENABLED_KEY = "usage-reporting.enabled";
    private static final String USAGE_REPORTING_ENDPOINT_KEY = "usage-reporting.endpoint";
    private static final String USAGE_REPORTING_KEY_KEY = "usage-reporting.key";
    private static final String DEFAULT_USAGE_REPORTING_ENDPOINT = "https://trace.danielstephenson.dev";

    private final SimpleSkills simpleSkills;

    private final boolean altered = false;
    private File configFile;
    private FileConfiguration config;

    public ConfigService(SimpleSkills simpleSkills) {
        this.simpleSkills = simpleSkills;
    }

    public void createConfig() {
        simpleSkills.getLogger().info("Creating config.yml file..."); // note: must use simpleSkills.getLogger() to avoid circular dependency issues
        configFile = new File(simpleSkills.getDataFolder(), "config.yml");

        if (!configFile.exists()) simpleSkills.saveResource("config.yml", false);
        load(configFile);
    }

    /** What createConfig() does once it knows the file; package-private so a test can hand it a file of its own. */
    void load(File file) {
        configFile = file;
        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            simpleSkills.getLogger().severe("Failed to load config.yml file."); // note: must use simpleSkills.getLogger() to avoid circular dependency issues
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException ignored) {
        }
    }

    /**
     * Set skill activation state in config and save
     * @param skillName name of the skill
     * @param active activation state
     */
    public void setSkillActive(String skillName, boolean active) {
        config.set("skills." + skillName + ".active", active);
        saveConfig();
    }

    /**
     * Puts the usage-reporting block on disk if the file does not have one.
     * createConfig() never touches an existing config.yml, so a server upgraded
     * in place from before usage reporting kept reporting through the bundled
     * defaults (see the getters below) with no visible switch to turn it off.
     * The values are copied from the jar's config.yml, not written as new
     * literals, and they go through this service's own configuration, because
     * that is what saveConfig() writes back on every disable -- a write through
     * the plugin's getConfig() alone would be lost at the next shutdown.
     */
    public void saveUsageReportingDefaultsIfNotPresent() {
        if (config == null || config.isSet(USAGE_REPORTING_SECTION)) {
            return;
        }
        Configuration defaults = simpleSkills.getConfig().getDefaults();
        if (defaults == null) {
            return;
        }
        config.set(USAGE_REPORTING_ENABLED_KEY, defaults.get(USAGE_REPORTING_ENABLED_KEY));
        config.set(USAGE_REPORTING_ENDPOINT_KEY, defaults.get(USAGE_REPORTING_ENDPOINT_KEY));
        config.set(USAGE_REPORTING_KEY_KEY, defaults.get(USAGE_REPORTING_KEY_KEY));
        saveConfig();
    }

    // The usage-reporting settings are read through the plugin's own getConfig()
    // rather than the FileConfiguration this service loads, and with the
    // one-argument getters, deliberately. createConfig() never touches a
    // config.yml that already exists, so a server upgraded from a version before
    // usage reporting has no usage-reporting block on disk. The YamlConfiguration
    // loaded above sees only that file, whereas Bukkit registers the jar's
    // config.yml as the defaults for the plugin's getConfig(), and the
    // one-argument getters fall through to them -- but the two-argument getters
    // return their explicit fallback instead, which for the key would be "" and
    // would turn reporting off on every existing installation. Verified against
    // YamlConfiguration, not assumed.

    public boolean isUsageReportingEnabled() {
        return simpleSkills.getConfig().getBoolean(USAGE_REPORTING_ENABLED_KEY);
    }

    public String getUsageReportingEndpoint() {
        String endpoint = simpleSkills.getConfig().getString(USAGE_REPORTING_ENDPOINT_KEY);
        return endpoint != null ? endpoint : DEFAULT_USAGE_REPORTING_ENDPOINT;
    }

    /** Empty when no key is configured or bundled, which the client treats as "off". */
    public String getUsageReportingKey() {
        String key = simpleSkills.getConfig().getString(USAGE_REPORTING_KEY_KEY);
        return key != null ? key : "";
    }

}