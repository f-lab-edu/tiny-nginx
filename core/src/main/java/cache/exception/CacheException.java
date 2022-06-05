package cache.exception;

/**
 * {@link cache.Cache}와 관련된 오류를 처리하기 위해 커스텀한 {@link CacheException} 클래스이다.
 */
public class CacheException extends RuntimeException {
	/**
	 * 지정된 세부 정보 메시지를 사용하여 새 런타임 예외를 구성한다.
	 * 원인은 초기화되지 않았다.
	 * @param message 세부 메시지
	 */
	public CacheException(String message) {
		super(message);
	}

	/**
	 * 지정된 세부 정보 메시지와 원인을 사용하여 새 런타임 예외를 구성한다.
	 * @param message 세부 메시지
	 * @param cause 원인
	 */
	public CacheException(String message, Throwable cause) {
		super(message, cause);
	}
}
