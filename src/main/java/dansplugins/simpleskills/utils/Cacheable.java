package dansplugins.simpleskills.utils;

/**
 * Interface for objects that can be cached
 * @author Daniel Stephenson
 */
public interface Cacheable {
    /**
     * Get the cache key for this object
     * @return The cache key
     */
    Object getKey();
}
