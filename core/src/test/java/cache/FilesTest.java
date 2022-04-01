package cache;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FilesTest {
	Files files;
	String path;

	@BeforeEach
	void setUp() {
		files = new Files();
		path = new File("src/test/java/cache").getAbsolutePath();
	}

	@Test
	@DisplayName("디렉토리 경로가 실제로 존재하면 true 이다.")
	void find_path() {
		Assertions.assertTrue(files.existPath(path));
	}
}
