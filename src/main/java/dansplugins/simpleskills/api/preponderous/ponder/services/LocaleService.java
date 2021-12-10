/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import preponderous.ponder.Ponder;
import preponderous.ponder.misc.Pair;

public class LocaleService {
    private Ponder ponder;
    private ArrayList<String> supportedLanguageIDs;
    private ArrayList<String> keys;
    private HashMap<String, String> strings;
    private String languageFolderPath;
    private String localizationFileName;
    private String localizationFilePath;
    private String currentLanguageID;

    public LocaleService(Ponder ponder) {
        this.ponder = ponder;
        this.keys = new ArrayList();
        this.strings = new HashMap();
    }

    public void initialize(ArrayList<String> supportedLanguageIDs, String pluginFolderPath, String pluginName, String defaultLanguageID) {
        this.supportedLanguageIDs = supportedLanguageIDs;
        this.languageFolderPath = pluginFolderPath + pluginName + "/";
        this.localizationFileName = defaultLanguageID + ".tsv";
        this.localizationFilePath = this.languageFolderPath + this.localizationFileName;
        this.currentLanguageID = defaultLanguageID;
    }

    public String getCurrentLanguageID() {
        return this.currentLanguageID;
    }

    public void setCurrentLanguageID(String ID) {
        this.currentLanguageID = ID;
    }

    public String getText(String key) {
        if (!this.keys.contains(key)) {
            return String.format("[key '%s' not found]", key);
        }
        return this.strings.get(key);
    }

    public void loadStrings() {
        if (this.isFilePresent(this.localizationFilePath)) {
            this.loadFromPluginFolder();
            if (this.ponder.isDebugEnabled()) {
                System.out.println("DEBUG: Loaded from plugin folder!");
            }
        } else {
            this.loadFromResource();
            if (this.ponder.isDebugEnabled()) {
                System.out.println("DEBUG: Loaded from resource!");
            }
        }
        if (this.ponder.isDebugEnabled()) {
            System.out.println(String.format(this.getText("KeysLoaded"), this.keys.size()));
        }
    }

    public void reloadStrings() {
        this.keys.clear();
        this.strings.clear();
        this.loadStrings();
    }

    public boolean isLanguageIDSupported(String ID) {
        return this.supportedLanguageIDs.contains(ID);
    }

    public String getSupportedLanguageIDsSeparatedByCommas() {
        String IDs = "";
        for (int i = 0; i < this.supportedLanguageIDs.size(); ++i) {
            IDs = IDs + this.supportedLanguageIDs.get(i);
            if (i == this.supportedLanguageIDs.size() - 1) continue;
            IDs = IDs + ", ";
        }
        return IDs;
    }

    public boolean isFilePresent(String path) {
        File file = new File(path);
        return file.exists();
    }

    public void loadFromPluginFolder() {
        File file = new File(this.localizationFilePath);
        try {
            this.loadFromFile(file);
            this.updateCurrentLocalLanguageFile();
            this.saveToPluginFolder();
        }
        catch (Exception e) {
            if (this.ponder.isDebugEnabled()) {
                System.out.println("DEBUG: Something went wrong loading from the plugin folder.");
            }
            e.printStackTrace();
        }
    }

    public void loadFromFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                Pair<String, String> pair = this.getPairFromLine(line);
                if (pair == null || this.strings.containsKey(pair.getLeft())) continue;
                this.strings.put(pair.getLeft(), pair.getRight());
                this.keys.add(pair.getLeft());
            }
        }
        catch (Exception e) {
            if (this.ponder.isDebugEnabled()) {
                System.out.println("DEBUG: Something went wrong loading from file!");
            }
            e.printStackTrace();
        }
    }

    public void updateCurrentLocalLanguageFile() {
        if (this.ponder.isDebugEnabled()) {
            System.out.println("DEBUG: LocaleManager is updating supported local language files.");
        }
        if (this.isLanguageIDSupported(this.currentLanguageID)) {
            InputStream inputStream = this.getResourceAsInputStream(this.currentLanguageID + ".tsv");
            this.loadMissingKeysFromInputStream(inputStream);
        } else {
            InputStream inputStream = this.getResourceAsInputStream("en-us.tsv");
            this.loadMissingKeysFromInputStream(inputStream);
        }
    }

    public InputStream getResourceAsInputStream(String fileName) {
        return this.ponder.getPlugin().getResource(fileName);
    }

    public void loadFromResource() {
        try {
            InputStream inputStream = this.getResourceAsInputStream(this.localizationFileName);
            this.loadMissingKeysFromInputStream(inputStream);
            this.saveToPluginFolder();
        }
        catch (Exception e) {
            if (this.ponder.isDebugEnabled()) {
                System.out.println("DEBUG: Error loading from resource!");
            }
            e.printStackTrace();
        }
    }

    public void loadMissingKeysFromInputStream(InputStream inputStream) {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(reader);
        br.lines().forEach(line -> {
            Pair<String, String> pair = this.getPairFromLine((String)line);
            if (pair != null && !this.strings.containsKey(pair.getLeft())) {
                this.strings.put(pair.getLeft(), pair.getRight());
                this.keys.add(pair.getLeft());
            }
        });
    }

    public Pair<String, String> getPairFromLine(String line) {
        String key = "";
        String value = "";
        int tabIndex = this.getIndexOfTab(line);
        if (tabIndex != -1) {
            key = line.substring(0, tabIndex);
            value = line.substring(tabIndex + 1);
            return new Pair<String, String>(key, value);
        }
        return null;
    }

    public int getIndexOfTab(String line) {
        for (int i = 0; i < line.length(); ++i) {
            if (line.charAt(i) != '\t') continue;
            return i;
        }
        return -1;
    }

    public void saveToPluginFolder() {
        this.sortKeys();
        try {
            File folder = new File(this.languageFolderPath);
            if (!folder.exists() && !folder.mkdir()) {
                if (this.ponder.isDebugEnabled()) {
                    System.out.println("DEBUG: Failed to create directory.");
                }
                return;
            }
            File file = new File(this.localizationFilePath);
            if (!file.exists() && !file.createNewFile()) {
                if (this.ponder.isDebugEnabled()) {
                    System.out.println("DEBUG: Failed to create file.");
                }
                return;
            }
            try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8));){
                for (String key : this.keys) {
                    output.write(key + "\t" + this.strings.get(key) + "\n");
                }
            }
            catch (Exception ex) {
                if (this.ponder.isDebugEnabled()) {
                    System.out.println("DEBUG: Failed to write to file.");
                }
            }
        }
        catch (Exception e) {
            if (this.ponder.isDebugEnabled()) {
                System.out.println("DEBUG: There was a problem saving the strings.");
            }
            e.printStackTrace();
        }
    }

    public void sortKeys() {
        Collections.sort(this.keys);
    }
}

