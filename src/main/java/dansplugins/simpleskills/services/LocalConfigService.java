package dansplugins.simpleskills.services;

/*
    To add a new config option, the following methods must be altered:
    - saveMissingConfigDefaultsIfNotPresent
    - setConfigOption()
    - sendConfigList()
 */

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Daniel Stephenson
 */
public class LocalConfigService {

    private static LocalConfigService instance;
    private boolean altered = false;

    private LocalConfigService() {

    }

    public static LocalConfigService getInstance() {
        if (instance == null) {
            instance = new LocalConfigService();
        }
        return instance;
    }

    public void saveMissingConfigDefaultsIfNotPresent() {
        // set version
        if (!getConfig().isString("version")) {
            getConfig().addDefault("version", SimpleSkills.getInstance().getVersion());
        } else {
            getConfig().set("version", SimpleSkills.getInstance().getVersion());
        }

        // save config options
        if (!isSet("debugMode")) { getConfig().set("debugMode", false); }
        if (!isSet("defaultMaxLevel")) { getConfig().set("defaultMaxLevel", 100); }
        if (!isSet("defaultBaseExperienceRequirement")) { getConfig().set("defaultBaseExperienceRequirement", 10); }
        if (!isSet("defaultExperienceIncreaseFactor")) { getConfig().set("defaultExperienceIncreaseFactor", 1.2); }
        if (!isSet("MCVersion.1_14")) { getConfig().set("MCVersion.1_14", false); }
        if (!isSet("MCVersion.1_16")) { getConfig().set("MCVersion.1_16", false); }
        if (!isSet("MCVersion.1_17")) { getConfig().set("MCVersion.1_17", false); }

        getConfig().options().copyDefaults(true);
        SimpleSkills.getInstance().saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {
        if (getConfig().isSet(option)) {
            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("defaultMaxLevel") ||
                       option.equalsIgnoreCase("defaultBaseExperienceRequirement")) {
                getConfig().set(option, Integer.parseInt(value));
                sender.sendMessage(ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("debugMode")) {
                getConfig().set(option, Boolean.parseBoolean(value));
                sender.sendMessage(ChatColor.GREEN + "Boolean set.");
            } else if (option.equalsIgnoreCase("defaultExperienceIncreaseFactor")) {
                getConfig().set(option, Double.parseDouble(value));
                sender.sendMessage(ChatColor.GREEN + "Double set.");
            } else {
                getConfig().set(option, value);
                sender.sendMessage(ChatColor.GREEN + "String set.");
            }

            // save
            SimpleSkills.getInstance().saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    public void sendConfigList(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Config List ===");
        sender.sendMessage(ChatColor.AQUA + "version: " + getConfig().getString("version")
                + ", debugMode: " + getString("debugMode")
                + ", defaultMaxLevel: " + getInt("defaultMaxLeveL")
                + ", defaultBaseExperienceRequirement: " + getInt("defaultBaseExperienceRequirement")
                + ", defaultExperienceIncreaseFactor: " + getDouble("defaultExperienceIncreaseFactor"));
    }

    public boolean hasBeenAltered() {
        return altered;
    }

    public FileConfiguration getConfig() {
        return SimpleSkills.getInstance().getConfig();
    }

    public boolean isSet(String option) {
        return getConfig().isSet(option);
    }

    public int getInt(String option) {
        return getConfig().getInt(option);
    }

    public int getIntOrDefault(String option, int defaultValue) {
        int toReturn = getInt(option);
        if (toReturn == 0) {
            return defaultValue;
        }
        return toReturn;
    }

    public boolean getBoolean(String option) {
        return getConfig().getBoolean(option);
    }

    public double getDouble(String option) {
        return getConfig().getDouble(option);
    }

    public double getDoubleOrDefault(String option, double defaultValue) {
        double toReturn = getDouble(option);
        if (toReturn == 0) {
            return defaultValue;
        }
        return toReturn;
    }

    public String getString(String option) {
        return getConfig().getString(option);
    }

    public String getStringOrDefault(String option, String defaultValue) {
        String toReturn = getString(option);
        if (toReturn == null) {
            return defaultValue;
        }
        return toReturn;
    }
}