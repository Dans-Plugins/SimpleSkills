/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.InvalidConfigurationException
 *  org.bukkit.configuration.MemorySection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.NumberConversions
 */
package preponderous.ponder.misc;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigurationFile {
    public static final boolean LOAD_FROM_JAR = true;
    public static final boolean CREATE_EMPTY_FILE = false;
    private final File file;
    private final String name;
    private final boolean preloadedResource;
    private YamlConfiguration yml;
    private JavaPlugin plugin;

    public ConfigurationFile() {
        this("config", true);
    }

    public ConfigurationFile(@NotNull String name, boolean preloadedResource, JavaPlugin plugin) {
        this(name, preloadedResource);
        this.initializePlugin(plugin);
    }

    public ConfigurationFile(@NotNull String name, boolean preloadedResource) {
        this(name, ".yml", preloadedResource);
    }

    public ConfigurationFile(@NotNull String name, @NotNull String extension, boolean preloadedResource) {
        this.name = name;
        this.preloadedResource = preloadedResource;
        this.file = new File(this.getPlugin().getDataFolder(), name + extension);
        this.reloadConfig();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void reloadConfig() {
        if (!this.file.exists()) {
            if (this.preloadedResource) {
                try {
                    this.getPlugin().saveResource(this.name + ".yml", false);
                }
                catch (IllegalArgumentException illegalArgumentException) {}
            } else {
                try {
                    if (this.file.createNewFile()) {
                        // empty if block
                    }
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
        this.loadConfiguration();
    }

    private void loadConfiguration() {
        this.yml = new YamlConfiguration();
        try {
            this.yml.loadFromString(Files.toString(this.file, StandardCharsets.UTF_8));
        }
        catch (IOException | InvalidConfigurationException throwable) {
            // empty catch block
        }
    }

    public void saveConfig() {
        try {
            this.yml.save(this.file);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Nullable
    public ConfigurationSection getSection(@NotNull String path) {
        return this.yml.getConfigurationSection(path);
    }

    public boolean hasSection(@NotNull String path) {
        return this.getSection(path) != null;
    }

    @NotNull
    public Object get(@NotNull String path) {
        return this.getOrDefault(path, "[" + path + " is missing]");
    }

    @NotNull
    public Object getOrDefault(@NotNull String path, @NotNull Object defaultValue) {
        return this.yml.get(path, defaultValue);
    }

    @NotNull
    public String getString(@NotNull String path) {
        return this.getStringOrDefault(path, "[" + path + " is missing]");
    }

    @NotNull
    public String getStringOrDefault(@NotNull String path, @NotNull String defaultValue) {
        return this.yml.getString(path, defaultValue);
    }

    public int getInt(@NotNull String path) {
        return this.yml.getInt(path);
    }

    public int getIntOrDefault(@NotNull String path, int defaultValue) {
        return this.yml.getInt(path, defaultValue);
    }

    public double getDouble(@NotNull String path) {
        return this.yml.getDouble(path);
    }

    public double getDoubleOrDefault(@NotNull String path, double defaultValue) {
        return this.yml.getDouble(path, defaultValue);
    }

    public float getFloat(@NotNull String path) {
        Class<MemorySection> memorySection = MemorySection.class;
        try {
            Method getDefault = memorySection.getDeclaredMethod("getDefault", String.class);
            Object val = getDefault.invoke((Object)this.yml, path);
            return this.getFloatOrDefault(path, val instanceof Number ? NumberConversions.toFloat((Object)val) : 0.0f);
        }
        catch (Exception ex) {
            return 0.0f;
        }
    }

    public float getFloatOrDefault(@NotNull String path, float defaultValue) {
        Object val = this.yml.get(path);
        return val instanceof Number ? NumberConversions.toFloat((Object)val) : defaultValue;
    }

    public long getLong(@NotNull String path) {
        return this.yml.getLong(path);
    }

    public long getLongOrDefault(@NotNull String path, long defaultValue) {
        return this.yml.getLong(path, defaultValue);
    }

    public boolean getBoolean(@NotNull String path) {
        return this.yml.getBoolean(path);
    }

    public boolean getBooleanOrDefault(@NotNull String path, boolean defaultValue) {
        return this.yml.getBoolean(path, defaultValue);
    }

    @NotNull
    public List<?> getList(@NotNull String path) {
        List list = this.yml.getList(path);
        return list == null ? new ArrayList() : list;
    }

    @NotNull
    public List<String> getStringList(@NotNull String path) {
        return this.yml.getStringList(path);
    }

    @NotNull
    public List<Integer> getIntList(@NotNull String path) {
        return this.yml.getIntegerList(path);
    }

    @NotNull
    public List<Long> getLongList(@NotNull String path) {
        return this.yml.getLongList(path);
    }

    @NotNull
    public List<Double> getDoubleList(@NotNull String path) {
        return this.yml.getDoubleList(path);
    }

    @NotNull
    public List<Float> getFloatList(@NotNull String path) {
        return this.yml.getFloatList(path);
    }

    @NotNull
    public List<Boolean> getBooleanList(@NotNull String path) {
        return this.yml.getBooleanList(path);
    }

    public void set(@NotNull String path, @Nullable Object value) {
        this.set(path, value, false);
    }

    public void set(@NotNull String path, @Nullable Object value, boolean save) {
        this.yml.set(path, value);
        if (save) {
            this.saveConfig();
        }
    }

    public void initializePlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }
}

