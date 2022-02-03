package cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CacheLoaderTest {
	CacheLoader cacheLoader;

	@BeforeEach
	void setUp() {
		String path = "/data/tiny-nginx-cache";
		cacheLoader = new CacheLoader(path);
	}

	@Test
	@DisplayName("path의 내용이 있으면 build 한다.")
	void build_with_path() {
		Assertions.assertTrue(cacheLoader.isPath());
	}

	@Test
	@DisplayName("path가 null이거나 내용이 없으면 build 하지 않는다.")
	void no_build_without_path() {
		cacheLoader = new CacheLoader(" ");
		Assertions.assertFalse(cacheLoader.isPath());
	}
}
