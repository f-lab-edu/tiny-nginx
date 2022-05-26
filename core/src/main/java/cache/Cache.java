package cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cache implements ICache<String, MetaValue> {

	private static final Logger logger = LoggerFactory.getLogger(Cache.class);
	private final ConcurrentMap<String, MetaValue> cache;
	private static Cache instance;

	public static Cache getInstance() {
		if (instance == null) {
			instance = new Cache();
		}
		return instance;
	}

	private Cache() {
		cache = new ConcurrentHashMap<>();
	}

	@Override
	public MetaValue get(String key) {
		logger.debug("Cache.get():: key={}, value.data={}", key, cache.get(key));
		return cache.get(key);
	}

	@Override
	public void put(String key, MetaValue value) {
		if (key == null) {
			return;
		}

		if (value == null) {
			cache.remove(key);
		}

		cache.put(key, value);
		logger.debug("Cache.put():: key={}, value={}", key, value);
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
	public void remove(String key) {
		cache.remove(key);
		logger.debug("Cache.remove():: key={}", key);
	}

	public int size() {
		return cache.size();
	}
}
