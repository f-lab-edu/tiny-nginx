package cache;

import static java.nio.file.Files.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Files {
	/**
	 * 디렉토리가 존재하는지 확인한다.
	 * @param path 디렉토리 경로
	 * @return 디렉토리 존재 여부
	 */
	public static boolean existPath(String path) {
		File dir = new File(path);
		return dir.exists();
	}

	/**
	 * 모든 파일 목록을 조회한다.
	 * @param path 디렉토리 경로
	 * @param fileList 파일 목록
	 * @return 파일 목록
	 */
	public static List<File> getAllFiles(String path, List<File> fileList) {
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
	public static byte[] fileToByteArray(File file) {
		byte[] fileContent = null;
		try {
			fileContent = readAllBytes(file.toPath());
			log.debug("file={}, content.length={}", file, fileContent.length);
		} catch (IOException e) {
			log.info("fileToByteArray() error={}", e.toString());
		}
		return fileContent;
	}
}
