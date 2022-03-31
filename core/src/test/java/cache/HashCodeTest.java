package cache;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HashCodeTest {
	HashCode hashCode;
	String input = "";

	@BeforeEach
	void setUp() {
		hashCode = new HashCode();
		input = "http://host.docker.internal:8080/app/index.html";
	}

	@Test
	@DisplayName("문자열을 MD5 해시 함수로 변환한 값이 올바르다.")
	void string_to_md5() {
		String md5 = "9ADCE49FC6E8AE19A23A0995EE13FEFB";
		assertEquals(hashCode.md5(input), md5.toLowerCase());
	}

	@Test
	@DisplayName("문자열을 SHA-256 해시 함수로 변환한 값이 올바르다.")
	void string_to_sha256() {
		String sha256 = "9319A8D1B8C7987A864EBA3F57E335D1B18FDB4C370CB73B0B29C51C33DA2360";
		assertEquals(hashCode.sha256(input), sha256.toLowerCase());
	}

	@Test
	@DisplayName("hashCode(), MD5, SHA-256으로 해싱한 값들 중 중복 발생하면 실패한다.")
	void hashing() {
		CSVReadeer csvReadeer = new CSVReadeer();
		List<String> urlList = csvReadeer.readCSV().stream().distinct().collect(Collectors.toList());

		List<String> hashCodeList = new ArrayList<>();
		List<String> md5List = new ArrayList<>();
		List<String> sha256List = new ArrayList<>();

		for (String url : urlList) {
			md5List.add(hashCode.md5(url));
			sha256List.add(hashCode.sha256(url));
			hashCodeList.add(String.valueOf(url.hashCode()));
		}

		Set<String> md5Set = new HashSet<>(md5List);
		Set<String> sha256Set = new HashSet<>(sha256List);
		Set<String> hashCodeSet = new HashSet<>(hashCodeList);

		assertAll("갯수가 일치하지 않으면 충돌 발생한 것",
			() -> assertEquals(md5List.size(), md5Set.size()),
			() -> assertEquals(sha256List.size(), sha256Set.size()),
			() -> assertEquals(hashCodeList.size(), hashCodeSet.size()));

	}

	class CSVReadeer {
		String filePath = "src/test/java/cache/urlSet.csv";

		List<String> readCSV() {
			List<String> urlList = new ArrayList<>();
			File csv = new File(filePath);
			BufferedReader br = null;
			String line;

			try {
				br = new BufferedReader(new FileReader(csv));
				while ((line = br.readLine()) != null) {
					String[] lineArr = line.split(",");
					urlList.add(lineArr[2]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return urlList;
		}
	}
}