package cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashCode {
	private static final Logger logger = LoggerFactory.getLogger(HashCode.class);

	/**
	 * MD5 알고리즘으로 문자열을 암호화한다.
	 * @param input 암호화할 문자열
	 * @return 암호화된 문자열
	 */
	public String md5(String input) {
		return create("MD5", input);
	}

	/**
	 * SHA256으로 문자열을 암호화한다.
	 * @param input 암호화할 문자열
	 * @return 암호화된 문자열
	 */
	public String sha256(String input) {
		return create("SHA-256", input);
	}

	/**
	 * HashCode 를 생성한다.
	 * @param algorithm 해시 알고리즘
	 * @param input 암호화할 문자열
	 * @return 암호화된 문자
	 */
	private String create(String algorithm, String input) {
		MessageDigest md = getMessageDigestInstance(algorithm);
		md.update(input.getBytes());    // MessageDigest 객체에 데이터 제공;
		byte[] byteData = md.digest();    // MessageDigest 계산
		return byteToHexString(byteData);
	}

	/**
	 * MessageDigest 객체를 생성한다.
	 * @param algorithm    해시 알고리즘
	 * @return MessageDigest 객체
	 */
	private MessageDigest getMessageDigestInstance(String algorithm) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			logger.debug("getMessageDigestInstance() error={}", e.toString());
		}

		return md;
	}

	/**
	 * byte 배열을 16진수 문자열로 변환한다.
	 * @param byteData    byte 배열
	 * @return 16진수로 변환된 문자열
	 */
	private String byteToHexString(byte[] byteData) {
		StringBuilder sb = new StringBuilder();

		// data & 0xff   -> 비트연산자 &을 수행하는 경우 비트 수가 넓은 곳에 맞춰서 낮은 비트를 가진 자료형을 확장하므로, byte는 32비트의 int형으로 강제 형변환이 된다.
		// 				    이 때, byteData의 비트 확장시 가장 앞의 비트가 0인 경우는 0으로, 1인 경우는 1로 모든 비트를 채우는데,
		//				    원본 값과 전혀 다른 값이 되어 버리기 때문에 불필요하게 채워진 1을 모두 0으로 바꿔주기 위해 &0xff 연산을 수행한다.
		// + 0x100       -> Integer.toString(n, 16) 메소드는 int형을 16진수의 문자열로 변환한다.
		//				    16이상의 숫자는 모두 2자리의 String으로 리턴, 16미만의 숫자는 1자리의 String으로 리턴되기 때문에
		//				    추후 String 데이터를 저장할 때 다른 문제가 생기는 것을 방지하기 위해 강제로 +0x100을 더하여 변형하는 것이다.
		// .substring(1) -> 0x100을 더해서 강제로 3자리의 String으로 변경했기 때문에 불필요하게 붙은 가장 앞의 1만 제거해주면 된다.

		for (byte data : byteData) {
			sb.append(Integer.toString((data & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
}
