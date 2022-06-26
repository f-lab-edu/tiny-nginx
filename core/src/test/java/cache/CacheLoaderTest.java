package cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cache.exception.CacheException;
import cache.exception.CacheExceptionMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheLoaderTest {
	CacheLoader cacheLoader;
	Cache cache = Cache.getInstance();
	String path = "src/test/java/cache";

	@BeforeEach
	void setUp() throws Exception {
		cacheLoader = new CacheLoader(path);
	}

	@Test
	@DisplayName("path의 내용이 있으면 빌드(run)할 수 있다.")
	void build_with_path() {
		Assertions.assertTrue(cacheLoader.isCorrectPath());
	}

	@Test
	@DisplayName("path가 null 이거나 내용이 없으면 exception 을 반환한다.")
	void throw_exception_without_path() {
		Assertions.assertAll("path 가 null 이거나 비어있으면 반드시 exception 을 반환한다.",
			() -> {
				CacheException exception = Assertions.assertThrows(CacheException.class, () -> new CacheLoader(""));
				Assertions.assertEquals(CacheExceptionMessage.CHECK_PATH_CORRECT, exception.getMessage());
			},
			() -> {
				CacheException exception = Assertions.assertThrows(CacheException.class, () -> new CacheLoader(null));
				Assertions.assertEquals(CacheExceptionMessage.CHECK_PATH_CORRECT, exception.getMessage());
			});
	}

	@Test
	@DisplayName("CacheLoader 실행 시 경로에 있는 파일 갯수와 생성된 Cache의 Size가 일치해야 한다.")
	void cacheLoader_run_test() {
		List<File> fileList = Files.getAllFiles(path, new ArrayList<>());
		int fileCount = 0;
		if (fileList != null) {
			fileCount = fileList.size();
		}

		cacheLoader.run();
		Assertions.assertEquals(cache.size(), fileCount);
	}
}
