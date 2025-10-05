package dansplugins.simpleskills.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON storage utility to replace Ponder's JsonWriterReader
 * @author Daniel Stephenson
 */
public class JsonStorage {
    private String filePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void initialize(String path) {
        this.filePath = path;
        // Create directory if it doesn't exist
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void writeOutFiles(List<Map<String, String>> data, String fileName) {
        try {
            File file = new File(filePath + fileName);
            file.getParentFile().mkdirs();
            
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                gson.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to write JSON file: " + fileName);
            e.printStackTrace();
        }
    }

    public ArrayList<HashMap<String, String>> loadDataFromFilename(String fullPath) {
        File file = new File(fullPath);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType();
            ArrayList<HashMap<String, String>> data = gson.fromJson(reader, listType);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Failed to read JSON file: " + fullPath);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
