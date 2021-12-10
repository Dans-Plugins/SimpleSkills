/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.misc;

import java.util.ArrayList;
import java.util.HashSet;
import preponderous.ponder.modifiers.Cacheable;

public class Cache {
    private HashSet<Cacheable> cache = new HashSet();
    private ArrayList<Cacheable> storage;

    public Cache(ArrayList<Cacheable> storage) {
        this.storage = storage;
    }

    public Cacheable lookup(Object key) {
        Cacheable object = this.checkCache(key);
        if (object == null) {
            return this.checkStorage(key);
        }
        return object;
    }

    private Cacheable checkCache(Object key) {
        for (Cacheable object : this.cache) {
            if (!object.getKey().equals(key)) continue;
            return object;
        }
        return null;
    }

    private Cacheable checkStorage(Object key) {
        Cacheable object = null;
        for (Cacheable o : this.storage) {
            if (!o.getKey().equals(key)) continue;
            object = o;
        }
        if (object != null) {
            this.cache.add(object);
        }
        return object;
    }
}

