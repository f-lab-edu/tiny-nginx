package cache;

import static java.nio.file.Files.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Files {

	private static final Logger logger = LoggerFactory.getLogger(Files.class);

	/**
	 * 디렉토리가 존재하는지 확인한다.
	 * @param path 디렉토리 경로
	 * @return 디렉토리 존재 여부
	 */
	public boolean existPath(String path) {
		File dir = new File(path);
		System.out.println(dir);
		return dir.exists();
	}

	/**
	 * 모든 파일 목록을 조회한다.
	 * @param path 디렉토리 경로
	 * @param fileList 파일 목록
	 * @return 파일 목록
	 */
	public List<File> getAllFiles(String path, List<File> fileList) {
		File directory = new File(path);
		File[] files = directory.listFiles();

		if (files == null) {
			return null;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				getAllFiles(file.getPath(), fileList);
			} else {
				fileList.add(file);
			}
		}

		return fileList;
	}

	/**
	 * 파일 데이터를 Byte 배열로 변환한다.
	 * @param file 변환할 파일
	 * @return Byte 배열로 변환된 파일 내용
	 */
	public byte[] fileToByteArray(File file) {
		byte[] fileContent = null;
		try {
			fileContent = readAllBytes(file.toPath());
			logger.debug("file={}, content.length={}", file, fileContent.length);
		} catch (IOException e) {
			logger.info("fileToByteArray() error={}", e.toString());
		}
		return fileContent;
	}
}
