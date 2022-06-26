package cache;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CacheTest {
	Cache cache = Cache.getInstance();

	@BeforeEach
	void setUp() {
		cache_cleanUp();
	}

	@Test
	@DisplayName("key 를 이용하여 저장된 meta 정보를 가져온다.")
	void cache_get_value() {
		String key = "test";
		MetaValue value = new MetaValue(new byte[] {'c', 'a', 'c', 'h', 'e'});
		cache.put(key, value);

		String key2 = "test2";
		MetaValue value2 = new MetaValue(new byte[] {'t', 'e', 's', 't'});
		cache.put(key2, value2);

		assertAll("cache get value test",
			() -> assertEquals(cache.get(key), value),
			() -> assertEquals(cache.get(key2), value2),
			() -> assertNotEquals(cache.get(key), value2));
	}

	@Test
	@DisplayName("1개의 정보가 들어있는 cache 에 새로운 cache 정보를 추가하면 크기가 2이 된다.")
	void cache_put_value() {
		String key = "test";
		MetaValue value = new MetaValue(new byte[] {'c', 'a', 'c', 'h', 'e'});
		cache.put(key, value);
		assertEquals(cache.size(), 1);

		String key3 = "test3";
		MetaValue value3 = new MetaValue(new byte[] {'d', 'a', 'm', 'i'});
		cache.put(key3, value3);
		assertEquals(cache.size(), 2);
	}

	@Test
	@DisplayName("Cache에 maxCacheSize보다 많은 수의 cache 정보를 추가해도 LRU 알고리즘에 의해 크기가 maxCacheSize와 동일하다.")
	void cache_put_value_max() {
		int i = 0;
		int cacheMaxSize = cache.getMaxCacheSize();
		while (i <= cacheMaxSize) {
			cache.put("cache" + i, new MetaValue(new byte[] {'t', 'e', 's', 't'}));
			i++;
		}

		assertEquals(cache.size(), cacheMaxSize);
		assertNull(cache.get("cache0"));
	}

	@Test
	@DisplayName("2개의 정보가 들어있는 cache 에서 1개의 정보를 삭제하면 크기가 1이 된다.")
	void cache_remove() {
		String key = "test";
		MetaValue value = new MetaValue(new byte[] {'c', 'a', 'c', 'h', 'e'});
		cache.put(key, value);

		String key2 = "test2";
		MetaValue value2 = new MetaValue(new byte[] {'t', 'e', 's', 't'});
		cache.put(key2, value2);

		cache.remove(key);
		assertEquals(cache.size(), 1);
	}

	@Test
	@DisplayName("cache 정보를 모두 삭제하면 크기가 0이 된다.")
	void cache_cleanUp() {
		cache.cleanUp();
		assertEquals(cache.size(), 0);
	}

	@Test
	@DisplayName("LRU 알고리즘이 적용되어 cacheMaxSize 보다 많이 cache 가 저장되면 가장 오래된 cache 정보가 제거된다.")
	void lru_remove() {
		int i = 0;
		int cacheMaxSize = cache.getMaxCacheSize();
		while (i <= cacheMaxSize) {
			cache.put("cache" + i, new MetaValue(new byte[] {'t', 'e', 's', 't'}));
			i++;
		}

		assertNull(cache.get("cache0"));
	}
}
