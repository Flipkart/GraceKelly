package lego.gracekelly.entities;

/**
 * The CacheEntry class is a simple java object that holds data required to get, put and invalidate a cache entry.
 * The generic parameter indicates the type of the object that'll be store against the given key.
 * @param <T>
 */
public final class CacheEntry<T>{

    private final T value;
    private final String key;
    private final long ttl;
    private final long epoch_timestamp;

    private CacheEntry() throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    };

    public CacheEntry(final String key, final T value, final long ttl){
        this.key = key;
        this.value = value;
        this.ttl = ttl;
        this.epoch_timestamp = System.currentTimeMillis()/1000;
    }

    public CacheEntry(final String key, final T value){
        this.key = key;
        this.value = value;
        this.ttl = 0;
        this.epoch_timestamp = 0;
    }

    /**
     * Get's the value of the CacheEntry
     * @return value of the CacheEntry
     */
    public T getValue() {
        return value;
    }

    /**
     * Get's the key for the CacheEntry
     * @return key of the CacheEntry
     */
    public String getKey() {
        return key;
    }

    /**
     * Get's the ttl in seconds of the CacheEntry
     * @return ttl in seconds of the CacheEntry
     */
    public long getTtl() {
        return ttl;
    }

    /**
     * Get's the epoch_timestamp of when the CacheEntry was created this and ttl of the CacheEntry provide adequate
     * information to invalidate a CacheEntry.
     * @return the epoch timestamp of when this CacheEntry instance was created
     */
    public long getEpoch_timestamp() {
        return epoch_timestamp;
    }
}
