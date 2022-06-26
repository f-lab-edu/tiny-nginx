package cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link Cache} 클래스는 캐시를 구현하는 데 필요한 작업을 최소화하기 위해,
 * {@link ICache} 인터페이스의 메소드를 재정의한다.
 */
@Slf4j
public class Cache implements ICache<String, MetaValue> {
	private static final int MAX_CACHE_SIZE = 30;	// 저장할 캐시의 최대 크기
	private final ConcurrentMap<String, MetaValue> cache;	// 캐시
	private final ConcurrentLinkedQueue<String> lruQueue;	// LRU 알고리즘 적용을 위한 큐
	private static Cache instance;

	/**
	 * 한 번만 생성된 객체를 가져온다.
	 * 객체가 null 일 경우, 새로운 객체를 메모리에 할당한다.
	 * @return 한 번만 생성된 {@code Cache} 객체
	 */
	public static Cache getInstance() {
		if (instance == null) {
			instance = new Cache();
		}
		return instance;
	}
  
	private Cache() {
		cache = new ConcurrentHashMap<>();
		lruQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public MetaValue get(String key) {
		MetaValue value = null;
		if (cache.containsKey(key)) {
			lruQueue.remove(key);
			value = cache.get(key);
			lruQueue.add(key);
		}
		log.debug("Cache.get():: key={}, value.data={}", key, value);
		return value;
	}

	@Override
	public void put(String key, MetaValue value) {
		if (key == null) {
			return;
		}

		if (cache.containsKey(key)) {
			lruQueue.remove(key);
		}

		if (lruQueue.size() == MAX_CACHE_SIZE) {
			String oldestKey = lruQueue.poll();
			cache.remove(oldestKey);
		}

		lruQueue.add(key);
		cache.put(key, value);
		log.debug("Cache.put():: key={}, value={}, lru={}", key, value, lruQueue);
	}

	@Override
	public void cleanUp() {
		cache.clear();
		log.debug("Cache.cleanUp():: cache size={}", cache.size());
	}

	@Override
	public void remove(String key) {
		if (cache.containsKey(key)) {
			cache.remove(key);
			lruQueue.remove(key);
		}
		log.debug("Cache.remove():: key={}", key);
	}

	@Override
	public int size() {
		return cache.size();
	}

	/**
	 * LRU 알고리즘을 적용하기 위한 저장할 캐시의 최대 크기를 의미하는 {@code MAX_CACHE_SIZE}를 반환한다.
	 * @return 저장할 캐시의 최대 크기
	 */
	public int getMaxCacheSize() {
		return MAX_CACHE_SIZE;
	}
}
