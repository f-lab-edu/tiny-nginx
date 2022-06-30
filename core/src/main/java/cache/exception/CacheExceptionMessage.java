package cache.exception;

/**
 * {@link CacheExceptionMessage} 클래스에는 {@link cache.Cache}와 관련된 오류를 처리하기 위한 세부 메시지가 정의된 클래스이다.
 * 모든 세부 메시지는 상수로 정의되어 있다.
 */
public class CacheExceptionMessage {
	public static final String CHECK_PATH_CORRECT = "Please check that the path is correct.";
	public static final String CHECK_STATS_COUNT_NUMBERS = "Items for statistics in the cache cannot use negative numbers.";
}
