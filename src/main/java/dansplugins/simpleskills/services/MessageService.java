package dansplugins.simpleskills.services;

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageService {
    private final SimpleSkills simpleSkills;

    private File langFile;
    private FileConfiguration lang;

    public MessageService(SimpleSkills simpleSkills) {
        this.simpleSkills = simpleSkills;
    }

    public void createlang() {
        langFile = new File(simpleSkills.getDataFolder(), "message.yml");

        if (!langFile.exists()) simpleSkills.saveResource("message.yml", false);
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
