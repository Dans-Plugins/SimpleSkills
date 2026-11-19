package dansplugins.simpleskills.config;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.trace.UsageReportingSetting;
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

    /**
     * Writes this service's configuration to config.yml without ever turning usage reporting back
     * on. Every write of config.yml goes through here (setSkillActive and the usage-reporting block
     * written on upgrade). A config.yml that could not be parsed was loaded as empty, so writing it
     * would replace the operator's whole file (an opt-out included) with whatever is in memory: it
     * is left alone and an error is logged. And the configuration in memory can be older than the
     * file, since an operator may set usage-reporting.enabled: false by hand while the server runs,
     * so an opt-out on disk is carried into it first.
     */
    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        if (!UsageReportingSetting.prepareToSave(config, configFile, simpleSkills.getLogger())) {
            return;
        }
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
     * that is what saveConfig() writes -- a write through the plugin's
     * getConfig() alone would be lost the next time this service saves.
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

    /**
     * Fails closed: only a value that clearly means "on" turns reporting on (getBoolean() would
     * read a quoted "false" as the bundled true), and config.yml as it is on disk right now must
     * allow it too, so neither a stale loaded copy nor Bukkit's fallback to the defaults for a
     * file it could not parse can override an opt-out.
     */
    public boolean isUsageReportingEnabled() {
        return UsageReportingSetting.isEnabled(simpleSkills.getConfig(), configFile);
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