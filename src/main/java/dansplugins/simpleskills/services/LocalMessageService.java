package dansplugins.simpleskills.services;

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LocalMessageService {

    private static LocalMessageService instance;
    private LocalMessageService() {

    }


    public static LocalMessageService getInstance() {
        if (instance == null) {
            instance = new LocalMessageService();
        }
        return instance;
    }


    private File langFile;
    private FileConfiguration lang;


    public void createlang() {
        langFile = new File(SimpleSkills.getInstance().getDataFolder(), "message.yml");

        if (!langFile.exists()) SimpleSkills.getInstance().saveResource("message.yml", false);
        lang = new YamlConfiguration();

        try {
            lang.load(langFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getlang() {
        return lang;
    }


    public void reloadlang() {
        lang = YamlConfiguration.loadConfiguration(langFile);
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
