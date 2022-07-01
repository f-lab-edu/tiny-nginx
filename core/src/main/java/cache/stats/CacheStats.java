package cache.stats;

import java.util.Objects;

import cache.exception.CacheException;
import static cache.exception.CacheExceptionMessage.*;

/**
 * {@link cache.Cache}의 성능에 대한 통계를 나타내는 클래스이다.
 */
public class CacheStats {
	private static final CacheStats EMPTY_STATS = of(0L, 0L, 0L, 0L, 0L, 0L);

	private final long hitCount;
	private final long missCount;
	private final long loadSuccessCount;
	private final long loadFailureCount;
	private final long totalLoadTime;
	private final long evictionCount;

	public CacheStats(long hitCount, long missCount, long loadSuccessCount, long loadFailureCount, long totalLoadTime,
		long evictionCount) {
		if (hitCount < 0 || missCount < 0 || loadSuccessCount < 0 ||
			loadFailureCount < 0 || totalLoadTime < 0 || evictionCount < 0) {
			throw new CacheException(CHECK_STATS_COUNT_NUMBERS);
		}
		this.hitCount = hitCount;
		this.missCount = missCount;
		this.loadSuccessCount = loadSuccessCount;
		this.loadFailureCount = loadFailureCount;
		this.totalLoadTime = totalLoadTime;
		this.evictionCount = evictionCount;
	}

	/**
	 * 지정된 통계를 나타내는 {@code CacheStats}를 반환한다.
	 * @param hitCount 캐시 적중 횟수
	 * @param missCount 캐시 누락 횟수
	 * @param loadSuccessCount 성공적인 캐시의 로드 수
	 * @param loadFailureCount 실패한 캐시의 로드 수
	 * @param totalLoadTime 총 로드 시간(성공 및 실패)
	 * @param evictionCount 캐시에서 제거된 항목의 수
	 * @return 지정된 통계를 나타내는 {@code CacheStats}
	 */
	public static CacheStats of(long hitCount, long missCount, long loadSuccessCount, long loadFailureCount,
		long totalLoadTime, long evictionCount) {
		return new CacheStats(hitCount, missCount, loadSuccessCount,
			loadFailureCount, totalLoadTime, evictionCount);
	}

	/**
	 * 기록되지 않은 빈 통계 인스턴스를 반환한다.
	 * @return 빈 통계 인스턴스
	 */
	public static CacheStats empty() {
		return EMPTY_STATS;
	}

	/**
	 * {@link cache.Cache} 조회 메소드가 캐시되거나 캐시되지 않은 값을 반환한 횟수인 {@code hitCount + missCount}를 반환한다.
	 * @return {@code hitCount + missCount}
	 */
	public long requestCount() {
		return saturatedAdd(hitCount, missCount);
	}

	/**
	 * {@link cache.Cache} 조회 메소드가 캐시된 값을 반환한 횟수를 반환한다.
	 * @return {@link cache.Cache} 조회 메소드가 캐시된 값을 반환한 횟수
	 */
	public long getHitCount() {
		return hitCount;
	}

	/**
	 * {@link cache.Cache} 조회 메소드가 캐시되지 않은(새로 로드된) 값 또는 null을 반환한 횟수를 반환한다.
	 * @return {@link cache.Cache} 조회 메소드가 캐시되지 않은(새로 로드된) 값 또는 null을 반환한 횟수
	 */
	public long getMissCount() {
		return missCount;
	}

	/**
	 * {@link cache.Cache} 조회 메소드가 새 값을 성공적으로 로드한 횟수를 반환한다.
	 * 이것은 항상 {@link #missCount}와 함께 증가하지만,
	 * {@code missCount}는 캐시 로드 중 예외가 발생하는 경우에도 증가한다({@link #loadFailureCount} 참조).
	 * @return {@link cache.Cache} 조회 메소드가 새 값을 성공적으로 로드한 횟수
	 */
	public long getLoadSuccessCount() {
		return loadSuccessCount;
	}

	/**
	 * 값을 찾을 수 없거나 로드하는 동안 예외가 발생하여 {@link cache.Cache} 조회 메소드가 새 값을 로드하지 못한 횟수를 반환한다.
	 * 캐시 로드가 성공적으로 완료되면 {@code missCount}도 증가하지만,
	 * 이는 항상 {@code missCount}와 함께 증가한다({@link #loadSuccessCount} 참조).
	 * @return {@link cache.Cache} 조회 메소드가 새 값을 로드하지 못한 횟수
	 */
	public long getLoadFailureCount() {
		return loadFailureCount;
	}

	/**
	 * 캐시가 새 값을 로드하는 데 사용한 총 시간(나노초)을 반환한다.
	 * 누락 패널티를 계산하는 데 사용할 수 있다.
	 * {@code totalLoadTime}은 {@code loadSuccessCount} 또는 {@code loadFailureCount}가 증가할 때마다 증가한다.
	 * @return 캐시가 새 값을 로드하는 데 사용한 총 시간(나노초)
	 */
	public long getTotalLoadTime() {
		return totalLoadTime;
	}

	/**
	 * 항목이 제거된 횟수를 반환한다.
	 * @return 항목이 퇴거된 횟수
	 */
	public long getEvictionCount() {
		return evictionCount;
	}

	/**
	 * 적중된 캐시 요청 비율을 반환한다. 이는 {@code hitCount / requestCount} 또는
	 * {@code requestCount == 0}일 때, {@code 1.0}으로 정의된다.
	 * @return 적중된 캐시 요청 비율
	 */
	public double getHitRate() {
		long requestCount = this.requestCount();
		return requestCount == 0 ? 1.0 : (double)hitCount / requestCount;
	}

	/**
	 * 누락된 캐시 요청 비율을 반환한다. 이는 {@code missCount / requestCount} 또는
	 * {@code requestCount == 0}일 때, {@code 0.0}으로 정의된다.
	 * @return 누락된 캐시 요청 비
	 */
	public double getMissRate() {
		long requestCount = requestCount();
		return requestCount == 0 ? 0.0 : (double)missCount / requestCount;
	}

	/**
	 * {@link cache.Cache} 조회 메소드가 새 값을 로드하려고 시도한 총 횟수를 반환한다.
	 * 여기에는 성공적인 로드 작업과 예외가 발생한 작업이 모두 포함되며,
	 * 이는 {@code loadSuccessCount + loadFailureCount}로 정의된다.
	 * @return {@code loadSuccessCount + loadFailureCount}
	 */
	public long loadCount() {
		return saturatedAdd(loadSuccessCount, loadFailureCount);
	}

	/**
	 * 예외를 던진 캐시 로딩 시도의 비율을 반환한다.
	 * 이는 {@code loadFailureCount / (loadSuccessCount + loadFailureCount)} 또는 {@code loadSuccessCount + loadFailureCount == 0} 일 때,
	 * {@code 0.0}으로 정의된다.
	 * @return 예외를 던진 캐시 로딩 시도의 비율
	 */
	public double loadFailureRate() {
		long totalLoadCount = this.saturatedAdd(loadSuccessCount, loadFailureCount);
		return totalLoadCount == 0 ? 0.0 : (double)loadFailureCount / totalLoadCount;
	}

	/**
	 * {@code CacheStats}와 {@code otherStats}의 차이를 나타내는 새로운 {@code CacheStats}를 반환한다.
	 * {@code CacheStats}에서 지원하지 않는 음수의 값은 0으로 반환한다.
	 * @param otherStats 뺄 통계
	 * @return {@code CacheStats}와 {@code otherStats}의 차이
	 */
	public CacheStats minus(CacheStats otherStats) {
		return of(
			Math.max(0L, hitCount - otherStats.hitCount),
			Math.max(0L, missCount - otherStats.missCount),
			Math.max(0L, loadSuccessCount - otherStats.loadSuccessCount),
			Math.max(0L, loadFailureCount - otherStats.loadFailureCount),
			Math.max(0L, totalLoadTime - otherStats.totalLoadTime),
			Math.max(0L, evictionCount - otherStats.evictionCount)
		);
	}

	/**
	 * {@code CacheStats}와 {@code otherStats}의 합계를 나타내는 새로운 {@code CacheStats}를 반환한다.
	 * @param otherStats 추가할 통계
	 * @return 통계의 합계
	 */
	public CacheStats plus(CacheStats otherStats) {
		return of(
			saturatedAdd(hitCount, otherStats.hitCount),
			saturatedAdd(missCount, otherStats.missCount),
			saturatedAdd(loadSuccessCount, otherStats.loadSuccessCount),
			saturatedAdd(loadFailureCount, otherStats.loadFailureCount),
			saturatedAdd(totalLoadTime, otherStats.totalLoadTime),
			saturatedAdd(evictionCount, otherStats.evictionCount)
		);
	}

	/**
	 * 오버플로나 언더플로되지 않는 한 {@code a + b}를 반환한다.
	 * 오버플로나 언더플로가 될 경우, 각각 {@code Long.MAX_VALUE} 또는 {@code Long.MIN_VALUE}가 반환된다.
	 * @param a 더할 값
	 * @param b 더할 값
	 * @return {@code a + b}
	 */
	private long saturatedAdd(long a, long b) {
		long sum = a + b;
		if (((a ^ b) < 0) | ((a ^ sum) >= 0)) {
			// a와 b의 부호가 다르거나, a가 sum과 같은 부호가 있다면, 오버플로가 발생하지 않았다면 sum을 반환한다.
			return sum;
		}

		// 오버플로/언더플로
		// 기호가 음수라면 MAX를 반환해야 하고, 그렇지 않으면 MIN을 반환해야 한다.
		return Long.MAX_VALUE + ((sum >>> (Long.SIZE - 1) ^ 1));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CacheStats stats = (CacheStats)o;
		return hitCount == stats.hitCount && missCount == stats.missCount && loadSuccessCount == stats.loadSuccessCount
			&& loadFailureCount == stats.loadFailureCount && totalLoadTime == stats.totalLoadTime
			&& evictionCount == stats.evictionCount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(hitCount, missCount, loadSuccessCount, loadFailureCount, totalLoadTime, evictionCount);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
			"hitCount=" + hitCount +
			", missCount=" + missCount +
			", loadSuccessCount=" + loadSuccessCount +
			", loadFailureCount=" + loadFailureCount +
			", totalLoadTime=" + totalLoadTime +
			", evictionCount=" + evictionCount +
			'}';
	}
}