package dansplugins.simpleskills.message;

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class MessageService {
    private static final String LANG_FILE_NAME = "message.yml";

    private final SimpleSkills simpleSkills;

    private File langFile;
    private FileConfiguration lang;

    public MessageService(SimpleSkills simpleSkills) {
        this.simpleSkills = simpleSkills;
    }

    public void createlang() {
        langFile = resolveLangFile();

        if (!langFile.exists()) simpleSkills.saveResource(LANG_FILE_NAME, false);
        lang = new YamlConfiguration();

        try {
            lang.load(langFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        applyBundledDefaults();
    }

    public FileConfiguration getlang() {
        return lang;
    }


    public void reloadlang() {
        lang = YamlConfiguration.loadConfiguration(langFile);
        applyBundledDefaults();
    }

    /**
     * Method to obtain the message file within the plugin's data folder.
     * <p>
     * The lookup is isolated here so that it can be overridden with a temporary file in tests,
     * as {@link org.bukkit.plugin.java.JavaPlugin#getDataFolder()} is final and cannot be mocked.
     * </p>
     *
     * @return the message file on disk.
     */
    File resolveLangFile() {
        return new File(simpleSkills.getDataFolder(), LANG_FILE_NAME);
    }

    /**
     * Method to obtain the message file bundled in the plugin jar.
     *
     * @return a stream over the bundled message file, or {@code null} if it isn't on the classpath.
     */
    InputStream openBundledLang() {
        return getClass().getClassLoader().getResourceAsStream(LANG_FILE_NAME);
    }

    /**
     * Method to back the loaded message file with the message file bundled in the plugin jar.
     * <p>
     * A message.yml written to disk by an older version of the plugin is never rewritten on
     * upgrade, so any message key introduced afterwards is absent from it and is looked up as
     * {@code null}. Callers wrap those lookups in {@link java.util.Objects#requireNonNull},
     * so a missing key throws inside a skill trigger and the whole event handler fails.
     * Registering the bundled file as the defaults makes such a key fall back to its shipped
     * value instead. Values present on disk still win, so player customisations are preserved,
     * and the defaults are not written back to disk by {@link #savelang()}.
     * </p>
     */
    private void applyBundledDefaults() {
        final InputStream bundledLang = openBundledLang();
        if (bundledLang == null) return;
        try (Reader reader = new InputStreamReader(bundledLang, StandardCharsets.UTF_8)) {
            lang.setDefaults(YamlConfiguration.loadConfiguration(reader));
        } catch (IOException ignored) {
        }
    }

    public void savelang() {
        try {
            lang.save(langFile);
        } catch (IOException ignored) {
        }
    }

    public String convert(String s) {
        return s.replaceAll("&", "§");
    }

}
