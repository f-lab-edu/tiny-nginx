package cache;

import static cache.exception.CacheExceptionMessage.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cache.exception.CacheException;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link CacheLoader} 클래스는 Master 에 의해 한 번만 실행되며,
 * 파일로 저장된 캐시 정보를 바탕으로 공유 메모리 영역에 캐시된 {@code key}와 {@code value}를 설정한다.
 */
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
			throw new CacheException(CHECK_PATH_CORRECT);
		}
	}

	/**
	 * 파일 경로를 나타내는 {@code path}가 올바른 경로인지 여부를 반환한다.
	 * @return 올바른 경로인지 여부 (올바르면 true, 그렇지 않으면 false)
	 */
	boolean isCorrectPath() {
		return path != null && !path.trim().equals("");
	}

	/**
	 * {@link CacheLoader}가 실행된다.
	 */
	public void run() {
		// 경로가 존재할 경우
		if (Files.existPath(path)) {
			List<File> fileList = Files.getAllFiles(path, new ArrayList<>());	// 경로 내의 모든 파일 목록을 조회한다.
			if (fileList != null) {	// 파일 목록이 null이 아니면
				for (File file : fileList) {
					String key = String.valueOf(file.toPath().hashCode());
					MetaValue value = new MetaValue(Files.fileToByteArray(file));
					cache.put(key, value);	// cache에 매핑한다.
				}
				log.info("CacheLoader.run():: files size = {}, cache size = {}", fileList.size(), cache.size());
			}
		}
	}
}