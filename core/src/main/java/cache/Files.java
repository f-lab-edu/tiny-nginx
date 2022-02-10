package cache;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Files {

	private static final Logger logger = LoggerFactory.getLogger(Files.class);
	private String path;

	public Files() {
	}

	public Files(String path) {
		this.path = path;
	}

	public boolean existPath() {
		File dir = new File(this.path);
		return dir.exists();
	}

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

		// logger.debug("fileList={}", fileList);
		return fileList;
	}

	/**
	 * Convert the file contents as byte array.
	 * @param file File
	 * @return File contents converted to byte array.
	 * @throws IOException throws IOException
	 */
	public byte[] convertFileToByteArray(File file) {
		byte[] fileContent = null;
		try {
			fileContent = java.nio.file.Files.readAllBytes(file.toPath());
			logger.debug("file={}, content.length={}", file, fileContent.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}

	/**
	 * Convert the file name as MD5.
	 * @param path File path
	 * @return File path converted to md5
	 */
	public String convertMd5(String path) {
		String md5;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(path.getBytes());
			byte[] byteData = md.digest();
			StringBuilder sb = new StringBuilder();

			for (byte data : byteData) {
				sb.append(Integer.toString((data & 0xff) + 0x100, 16).substring(1));
			}
			md5 = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			md5 = null;
		}

		logger.debug("path={}, hashcode={}", path, md5);
		return md5;
	}
}
