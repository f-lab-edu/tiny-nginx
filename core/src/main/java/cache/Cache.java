package cache;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cache implements ICache<Key, MetaValue> {

	private static final Logger logger = LoggerFactory.getLogger(Cache.class);
	private final Map<Key, MetaValue> cache;

	public Cache() {
		cache = new HashMap<>();
	}

	@Override
	public MetaValue get(Key key) {
		logger.debug("Cache.get():: key={}, value.data={}", key.getKey(), cache.get(key));
		return cache.get(key);
	}

	@Override
	public void put(Key key, MetaValue value) {
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
