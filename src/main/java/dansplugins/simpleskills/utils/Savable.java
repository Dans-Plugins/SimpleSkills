package dansplugins.simpleskills.utils;

import java.util.Map;

/**
 * Interface for objects that can be saved to storage
 * @author Daniel Stephenson
 */
public interface Savable {
    /**
     * Save the object to a map
     * @return Map of string keys to string values
     */
    Map<String, String> save();

    /**
     * Load the object from a map
     * @param data Map of string keys to string values
     */
    void load(Map<String, String> data);
}
