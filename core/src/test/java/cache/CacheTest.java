package cache;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CacheTest {

	Cache cache = new Cache();
	String key, key2;
	MetaValue value, value2;

	@BeforeEach
	void setUp() {
		key = "test";
		value = new MetaValue(new byte[] {'c', 'a', 'c', 'h', 'e'});
		cache.put(key, value);

		key2 = "test2";
		value2 = new MetaValue(new byte[] {'t', 'e', 's', 't'});
		cache.put(key2, value2);
	}

	@Test
	@DisplayName("key 를 이용하여 저장된 meta 정보를 가져온다.")
	void cache_get_value() {
		assertAll("cache get value test",
			() -> assertEquals(cache.get(key), value),
			() -> assertEquals(cache.get(key2), value2),
			() -> assertNotEquals(cache.get(key), value2));
	}

	@Test
	@DisplayName("새로운 cache 정보를 추가하면 크기가 3이 된다.")
	void cache_put_value() {
		String key3 = "test3";
		MetaValue value3 = new MetaValue(new byte[] {'d', 'a', 'm', 'i'});
		cache.put(key3, value3);

		assertEquals(cache.size(), 3);
	}

	@Test
	@DisplayName("cache 정보를 1개 삭제하면 크기가 1이 된다.")
	void cache_remove() {
		cache.remove(key);

		assertAll("cache put value test",
			() -> assertEquals(cache.size(), 1),
			() -> assertNull(cache.get(key)),
			() -> assertEquals(cache.get(key2), value2));
	}

	@Test
	@DisplayName("cache 정보를 모두 삭제하면 크기가 0이 된다.")
	void cache_cleanUp() {
		cache.cleanUp();

		assertAll("cache cleanUp test",
			() -> assertEquals(cache.size(), 0),
			() -> assertNull(cache.get(key)),
			() -> assertNull(cache.get(key2)));
	}
}
