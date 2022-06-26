package cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cache.exception.CacheException;
import cache.exception.CacheExceptionMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheLoader {
	private String path;
	// private static final int LOADER_FILES = 100;	// 한 번에 처리할 파일
	// private static final int LOADER_THRESHOLD = 200;	// 각 로드의 실행 시간 (ms)
	// private static final int LOADER_SLEEPS = 50;	// 각 로드의 지연 시간 (ms)
	private Cache cache = Cache.getInstance();

	public CacheLoader(String path) {
		this.path = path;
		if (!isCorrectPath()) {
			log.debug("CacheLoader incorrect path => {}", path);
			throw new CacheException(CacheExceptionMessage.CHECK_PATH_CORRECT);
		}
	}

	boolean isCorrectPath() {
		return path != null && !path.trim().equals("");
	}

	public void run() {
		if (Files.existPath(path)) {
			List<File> fileList = Files.getAllFiles(path, new ArrayList<>());
			if (fileList != null) {
				for (File file : fileList) {
					String key = String.valueOf(file.toPath().hashCode());
					MetaValue value = new MetaValue(Files.fileToByteArray(file));
					cache.put(key, value);
				}
				log.info("CacheLoader.run():: files size = {}, cache size = {}", fileList.size(), cache.size());
			}
		}
	}
}