package cache;

public interface ICache<K, V> {
	V get(K key);

	void put(K key, V value);

	CacheStatus status();

	void cleanUp();

	void remove(K key);
}
