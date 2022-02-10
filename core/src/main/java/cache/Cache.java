package cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cache implements ICache<Key, MetaValue> {

	private static final Logger logger = LoggerFactory.getLogger(Cache.class);
	private final ConcurrentMap<Key, MetaValue> cache;

	public Cache() {
		cache = new ConcurrentHashMap<>();
	}

	@Override
	public MetaValue get(Key key) {
		logger.debug("Cache.get():: key={}, value.data={}", key.getKey(), cache.get(key));
		return cache.get(key);
	}

	@Override
	public void put(Key key, MetaValue value) {
		if (key == null) {
			return;
		}

		if (value == null) {
			cache.remove(key);
		}

		cache.put(key, value);
		logger.debug("Cache.put():: key={}, value={}", key.getKey(), value);
	}

	@Override
	public CacheStatus status() {
		return null;
	}

	@Override
	public void cleanUp() {
		cache.clear();
		logger.debug("Cache.cleanUp():: cache size={}", cache.size());
	}

	@Override
	public void remove(Key key) {
		cache.remove(key);
		logger.debug("Cache.remove():: key={}", key.getKey());
	}

	public int size() {
		return cache.size();
	}
}
