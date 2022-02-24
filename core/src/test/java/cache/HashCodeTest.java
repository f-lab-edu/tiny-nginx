package cache;

import static org.junit.jupiter.api.Assertions.*;

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
	@DisplayName("문자열을 MD5 해시 함수로 변환한다.")
	void string_to_md5() {
		String md5 = "9ADCE49FC6E8AE19A23A0995EE13FEFB";

		assertEquals(hashCode.md5(input), md5.toLowerCase());
	}
}