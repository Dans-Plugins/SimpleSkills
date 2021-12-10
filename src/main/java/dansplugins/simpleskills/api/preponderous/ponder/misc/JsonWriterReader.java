/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 */
package preponderous.ponder.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonWriterReader {
    private static String FILE_PATH;
    private static Type LIST_MAP_TYPE;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonWriterReader() {
        LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();
    }

    public void initialize(String filePath) {
        FILE_PATH = filePath;
    }

    public boolean writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(FILE_PATH);
            parentFolder.mkdir();
            File file = new File(FILE_PATH, fileName);
            file.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(this.gson.toJson(saveData));
            outputStreamWriter.close();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonReader reader = new JsonReader((Reader)new InputStreamReader((InputStream)new FileInputStream(filename), StandardCharsets.UTF_8));
            return (ArrayList)gson.fromJson(reader, LIST_MAP_TYPE);
        }
        catch (FileNotFoundException fileNotFoundException) {
            return new ArrayList<HashMap<String, String>>();
        }
    }
}

