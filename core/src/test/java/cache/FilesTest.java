package cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FilesTest {
	Files files;
	String path = "/Users/laura/Works/Git/tiny-nginx-cache";

	@BeforeEach
	void setUp() {
		files = new Files(path);
	}

	@Test
	@DisplayName("path이 실제로 존재하면 true 이다.")
	void find_path() {
		Assertions.assertTrue(files.existPath());
	}

	@Test
	@DisplayName("경로의 모든 파일을 찾는다.")
	void find_files() {
		List<File> fileList = new ArrayList<>();
		files.getAllFiles(path, fileList);
	}

	@Test
	@DisplayName("file을 byte array로 변환한다.")
	void file_to_byte_array() {
		List<File> fileList = new ArrayList<>();
		fileList = files.getAllFiles(path, fileList);

		for (File file : fileList) {
			files.convertFileToByteArray(file);
		}
	}

	@Test
	@DisplayName("파일 경로를 MD5로 Hashing 한다.")
	void file_path_convert_md5() {
		List<File> fileList = new ArrayList<>();
		fileList = files.getAllFiles(path, fileList);

		for (File file : fileList) {
			files.convertMd5(file.getPath());
		}
	}
}
