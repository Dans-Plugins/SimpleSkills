package dansplugins.simpleskills.api.preponderous.ponder.misc;

import com.google.common.io.Files;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The ConfigurationFile class stands for one Configuration File.
 * <p>
 *     The default operation of this class is a bridge between BukkitAPI and this API.
 *     <br>The default values of this class are 'config.yml' and 'load from jar'.
 * </p>
 * @author Callum Johnson
 * @since 14/06/2021 - 09:34
 */
public class ConfigurationFile {

    /**
     * Constant which can be used throughout the creation of a Configuration File.
     */
    public static final boolean LOAD_FROM_JAR = true;

    /**
     * Constant which can be used throughout the creation of a Configuration File.
     */
    public static final boolean CREATE_EMPTY_FILE = false;

    /**
     * The file related to the ConfigurationFile.
     */
    private final File file;

    /**
     * The name of the ConfigurationFile.
     */
    private final String name;

    /**
     * Boolean used when loading the File to load from the Jar or just create an empty file.
     */
    private final boolean preloadedResource;

    /**
     * The YamlConfiguration value generated at runtime.
     */
    private YamlConfiguration yml;

    /**
     * The plugin that is referenced when this configuration file is used.
     */
    private JavaPlugin plugin;

    /**
     * Constructor to generate the default ConfigurationFile.
     * <p>
     *     The default name is 'config.yml' and it loads it from the Jar.
     * </p>
     */
    public ConfigurationFile() {
        this("config", LOAD_FROM_JAR);
    }

    /**
     * Constructor to generate a ConfigurationFile and initialize it with a plugin.
     *
     * @author Daniel Stephenson
     * @since 10/27/2021
     *
     * @param name of the file.
     * @param preloadedResource or not. {@code true} if the Jar contains a default version of this file.
     * @param plugin {@link JavaPlugin} used to initialize the configuration file.
     */
    public ConfigurationFile(@NotNull String name, boolean preloadedResource, JavaPlugin plugin) {
        this(name, preloadedResource);
        initializePlugin(plugin);
    }

    /**
     * Constructor to generate a ConfigurationFile.
     *
     * @param name of the file.
     * @param preloadedResource or not. {@code true} if the Jar contains a default version of this file.
     */
    public ConfigurationFile(@NotNull String name, boolean preloadedResource) {
        this(name, ".yml", preloadedResource);
    }

    /**
     * Constructor to generate a ConfigurationFile.
     *
     * @param name of the file.
     * @param extension of the file.
     * @param preloadedResource or not. {@code true} if the Jar contains a default version of this file.
     */
    public ConfigurationFile(@NotNull String name, @NotNull String extension, boolean preloadedResource) {
        this.name = name;
        this.preloadedResource = preloadedResource;
        this.file = new File(getPlugin().getDataFolder(), name + extension);
        reloadConfig(); // Reload the configuration (create and then load).
    }

    /**
     * Method to reload the configuration file.
     * <p>
     *     This method uses {@link #preloadedResource} and {@link File#exists()} to determine whether or not to
     *     create a brand new file {@link File#createNewFile()} or to use {@link JavaPlugin#saveResource(String, boolean)}
     *     to save the file from the Jar onto the System.
     *     <br>This method, once the file has been saved, then loads the file into the {@link YamlConfiguration} variable
     *     {@link #yml}.
     * </p>
     *
     * @see #loadConfiguration()
     */
    private void reloadConfig() {
        if (!file.exists()) {
            if (preloadedResource) {
                try {
                    getPlugin().saveResource(name + ".yml", false);
                } catch (IllegalArgumentException exception) {
                    // getPlugin().exception(exception, "Resource path '" + name + "' is invalid."); // TODO: fix if necessary
                }
            } else {
                try {
                    if (!file.createNewFile()) {
                        // getPlugin().error("Failed to create the ConfigurationFile '" + name + "'!"); // TODO: fix if necessary
                    }
                } catch (IOException exception) {
                    // getPlugin().exception(exception, "IO Error whilst reloading '" + name + "'!"); // TODO: fix if necessary
                }
            }
        }
        loadConfiguration();
    }

/*

    /**
     * Method to load the ConfigurationFile into the {@link #yml} variable.
     * <p>
     *     This method attempts to use StableAPI {@link FileUtils#readFileToString(File, Charset)}, if this fails
     *     the method will then attempt to use UnstableAPI {@link Files#toString(File, Charset)}.
     *     <br>If both methods fail, both Exceptions are printed to the console window.
     * </p>
     */
/*    @SuppressWarnings("UnstableApiUsage")
    private void loadConfiguration() {
        yml = new YamlConfiguration();
        try {
            yml.loadFromString(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
        } catch (InvalidConfigurationException | IOException e) {
            getPlugin().warn("Failed to load the ConfigurationFile '" + name + "', defaulting to UnstableAPI.");
            try {
                yml.loadFromString(Files.toString(file, StandardCharsets.UTF_8));
            } catch (InvalidConfigurationException | IOException ex) {
                getPlugin().exception(e, "Failed to load ConfigurationFile with StableAPI.");
                getPlugin().exception(ex, "Failed to load ConfigurationFile with UnstableAPI.");
            }
        }
    }

*/

    /**
     * Method to load the ConfigurationFile into the {@link #yml} variable.
     * <p>
     *     The method will attempt to use UnstableAPI {@link Files#toString(File, Charset)}.
     *     <br>If this fails, an exception is printed to the console window.
     * </p>
     */
    @SuppressWarnings("UnstableApiUsage")
    private void loadConfiguration() {
        yml = new YamlConfiguration();
        try {
            yml.loadFromString(Files.toString(file, StandardCharsets.UTF_8));
        } catch (InvalidConfigurationException | IOException ex) {
            // getPlugin().exception(e, "Failed to load ConfigurationFile with StableAPI."); // TODO: fix if necessary
            // getPlugin().exception(ex, "Failed to load ConfigurationFile with UnstableAPI."); // TODO: fix if necessary
        }
    }

    /**
     * Method to save the Configuration File.
     *
     * @see YamlConfiguration#save(File)
     */
    public void saveConfig() {
        try {
            yml.save(file);
        } catch (IOException exception) {
            // getPlugin().exception(exception, "Failed to save the ConfigurationFile '" + name + "'"); // TODO: fix if necessary
        }
    }

    /**
     * Method to obtain a {@link ConfigurationSection} from path.
     *
     * @param path to obtain the section from.
     * @return {@link ConfigurationSection} or {@code null} (If not found).
     * @see #hasSection(String)
     */
    @Nullable
    public ConfigurationSection getSection(@NotNull String path) {
        return yml.getConfigurationSection(path);
    }

    /**
     * Method to determine if the Configuration File has the specified ConfigurationSection.
     *
     * @param path to determine existence.
     * @return {@code true} if it does.
     */
    public boolean hasSection(@NotNull String path) {
        return getSection(path) != null;
    }

    /**
     * Method to get something at the specified path.
     *
     * @param path of the data.
     * @return {@link Object} never, {@code null}.
     */
    @NotNull
    public Object get(@NotNull String path) {
        return getOrDefault(path, "[" + path + " is missing]");
    }

    /**
     * Method to get something at the specified path.
     *
     * @param path of the data.
     * @param defaultValue to send back if there is no data found.
     * @return {@link Object} never, {@code null}.
     */
    @NotNull
    public Object getOrDefault(@NotNull String path, @NotNull Object defaultValue) {
        return yml.get(path, defaultValue);
    }

    /**
     * Method to obtain a String from the Configuration using Yaml Methods.
     * <p>
     *     If the String isn't found at the path specified, it defaults to 'path is missing' to help with development.
     * </p>
     *
     * @param path to obtain the value at.
     * @return String regardless of existence.
     * @see #getStringOrDefault(String, String)
     */
    @NotNull
    public String getString(@NotNull String path) {
        return getStringOrDefault(path, "[" + path + " is missing]");
    }

    /**
     * Method to obtain a String or return a Default Value if not found.
     *
     * @param path to obtain the value at.
     * @param defaultValue if the value isn't found.
     * @return String regardless of existence.
     */
    @NotNull
    public String getStringOrDefault(@NotNull String path, @NotNull String defaultValue) {
        return yml.getString(path, defaultValue);
    }

    /**
     * Method to obtain an integer at the given path.
     *
     * @param path to get the value at.
     * @return {@link Integer} or {@code 0}
     */
    public int getInt(@NotNull String path) {
        return yml.getInt(path);
    }

    /**
     * Method to obtain an integer at the given path or a default value if not found.
     *
     * @param path to get the value at.
     * @param defaultValue to return if not found.
     * @return {@link Integer} or the defaultValue.
     */
    public int getIntOrDefault(@NotNull String path, int defaultValue) {
        return yml.getInt(path, defaultValue);
    }

    /**
     * Method to obtain a double at the given path.
     *
     * @param path to get the value at.
     * @return {@link Double} or {@code 0.0D}
     */
    public double getDouble(@NotNull String path) {
        return yml.getDouble(path);
    }

    /**
     * Method to obtain a double at the given path or a default value if not found.
     *
     * @param path to get the value at.
     * @param defaultValue to return if not found.
     * @return {@link Double} or the defaultValue.
     */
    public double getDoubleOrDefault(@NotNull String path, double defaultValue) {
        return yml.getDouble(path, defaultValue);
    }

    /**
     * Method to obtain a float at the given path.
     *
     * @param path to get the value at.
     * @return {@link Float} or {@code 0.0F}
     */
    public float getFloat(@NotNull String path) {
        final Class<MemorySection> memorySection = MemorySection.class;
        try {
            final Method getDefault = memorySection.getDeclaredMethod("getDefault", String.class);
            // if (!getDefault.canAccess(yml)) getDefault.setAccessible(true); // TODO: fix if necessary
            final Object val = getDefault.invoke(yml, path);
            return getFloatOrDefault(path, val instanceof Number ? NumberConversions.toFloat(val) : 0.0F);
        } catch (Exception ex) {
            // getPlugin().exception(ex, "Experienced an Exception, returning 0.0F"); // TODO: fix if necessary
            return 0.0F;
        }
    }

    /**
     * Method to obtain a float at the given path or a default value if not found.
     *
     * @param path to get the value at.
     * @param defaultValue to return if not found.
     * @return {@link Float} or the defaultValue.
     */
    public float getFloatOrDefault(@NotNull String path, float defaultValue) {
        final Object val = yml.get(path);
        return val instanceof Number ? NumberConversions.toFloat(val) : defaultValue;
    }

    /**
     * Method to obtain a long at the given path.
     *
     * @param path to get the value at.
     * @return {@link Long} or {@code 0L}
     */
    public long getLong(@NotNull String path) {
        return yml.getLong(path);
    }

    /**
     * Method to obtain a long at the given path or a default value if not found.
     *
     * @param path to get the value at.
     * @param defaultValue to return if not found.
     * @return {@link Long} or the defaultValue.
     */
    public long getLongOrDefault(@NotNull String path, long defaultValue) {
        return yml.getLong(path, defaultValue);
    }

    /**
     * Method to obtain a boolean at the given path.
     *
     * @param path to get the value at.
     * @return {@link Boolean} or {@code false}
     */
    public boolean getBoolean(@NotNull String path) {
        return yml.getBoolean(path);
    }

    /**
     * Method to obtain a boolean at the given path or a default value if not found.
     *
     * @param path to get the value at.
     * @param defaultValue to return if not found.
     * @return {@link Boolean} or the defaultValue.
     */
    public boolean getBooleanOrDefault(@NotNull String path, boolean defaultValue) {
        return yml.getBoolean(path, defaultValue);
    }

    /**
     * Method to obtain a list at the given path.
     *
     * @param path to get the value at.
     * @return {@link List}
     */
    @NotNull
    public List<?> getList(@NotNull String path) {
        final List<?> list = yml.getList(path);
        return list == null ? new ArrayList<>() : list;
    }

    /**
     * Method to obtain a string-list at the given path.
     *
     * @param path to get the value at.
     * @return {@link List} of {@link String}
     */
    @NotNull
    public List<String> getStringList(@NotNull String path) {
        return yml.getStringList(path);
    }

    /**
     * Method to obtain a int-list at the given path.
     *
     * @param path to get the value at.
     * @return {@link List} of {@link Integer}
     */
    @NotNull
    public List<Integer> getIntList(@NotNull String path) {
        return yml.getIntegerList(path);
    }

    /**
     * Method to obtain a long-list at the given path.
     *
     * @param path to get the value at.
     * @return {@link List} of {@link Long}
     */
    @NotNull
    public List<Long> getLongList(@NotNull String path) {
        return yml.getLongList(path);
    }

    /**
     * Method to obtain a double-list at the given path.
     *
     * @param path to get the value at.
     * @return {@link List} of {@link Double}
     */
    @NotNull
    public List<Double> getDoubleList(@NotNull String path) {
        return yml.getDoubleList(path);
    }

    /**
     * Method to obtain a float-list at the given path.
     *
     * @param path to get the value at.
     * @return {@link List} of {@link Float}
     */
    @NotNull
    public List<Float> getFloatList(@NotNull String path) {
        return yml.getFloatList(path);
    }

    /**
     * Method to obtain a boolean-list at the given path.
     *
     * @param path to get the value at.
     * @return {@link List} of {@link Boolean}
     */
    @NotNull
    public List<Boolean> getBooleanList(@NotNull String path) {
        return yml.getBooleanList(path);
    }

    /**
     * Method to set the value at a given path.
     * <p>
     *     This method calls {@link #set(String, Object, boolean)} with a default of {@code false}.
     * </p>
     *
     * @param path to set as value.
     * @param value to set at the location of the path.
     * @see #set(String, Object, boolean)
     */
    public void set(@NotNull String path, @Nullable Object value) {
        this.set(path, value, false);
    }

    /**
     * Method to set the value at a given path and optionally save it, when done.
     *
     * @param path to set as value.
     * @param value to set at the location of the path.
     * @param save to determine if the plugin should save after the value has been set.
     * @see #saveConfig()
     */
    public void set(@NotNull String path, @Nullable Object value, boolean save) {
        yml.set(path, value);
        if (save) saveConfig();
    }

    /**
     * Method to initialize the plugin for the configuration file.
     *
     * @author Daniel Stephenson
     * @since 10/27/2021
     */
    public void initializePlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Method to get the plugin associated with the configuration file.
     *
     * @author Daniel Stephenson
     * @since 10/27/2021
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

}
