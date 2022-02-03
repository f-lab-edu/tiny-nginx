package cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheLoader implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CacheLoader.class);
	private String path;

	public CacheLoader(String path) {
		this.path = path;
		if (isPath()) {
			this.run();
		}
	}

	@Override
	public void run() {
		// todo : cacheloader 동작
	}

	public boolean isPath() {
		return path != null && !path.trim().equals("");
	}

	public String getPath() {
		return path;
	}
}
