package cache.stats;

import java.util.concurrent.atomic.LongAdder;

/**
 * {@link cache.Cache}가 사용하기 위한 스레드 안전한 {@code StatsCounter} 클래스
 */
public class StatsCounter {
	private final LongAdder hitCount;
	private final LongAdder missCount;
	private final LongAdder loadSuccessCount;
	private final LongAdder loadFailureCount;
	private final LongAdder totalLoadTime;
	private final LongAdder evictionCount;

	/**
	 * 모든 카운트가 0으로 초기화된 인스턴스를 생성한다.
	 */
	public StatsCounter() {
		hitCount = new LongAdder();
		missCount = new LongAdder();
		loadSuccessCount = new LongAdder();
		loadFailureCount = new LongAdder();
		totalLoadTime = new LongAdder();
		evictionCount = new LongAdder();
	}

	/**
	 * 적중한 캐시를 기록한다. 캐시 요청이 캐시된 값을 반환할 때 호출된다.
	 * @param count 기록할 적중 횟수
	 */
	public void recordHit(int count) {
		hitCount.add(count);
	}

	/**
	 * 캐시 누락을 기록한다. 캐시 요청이 캐시에서 찾을 수 없는 값을 반환할 때 호출된다.
	 * @param count 기록할 누락 횟수
	 */
	public void recordMiss(int count) {
		missCount.add(count);
	}

	/**
	 * 새 항목의 성공적인 로드를 기록한다. 캐시 요청으로 인해 항목이 로드되고(예: {@link cache.Cache#get}),
	 * 로드가 성공적으로 완료되면 호출된다.
	 * {@link #recordMiss}와는 로드 스레드에서만 호출된다.
	 * @param loadTime 캐시가 새 값을 계산하거나 검색하는 데 걸린 시간(나노초)
	 */
	public void recordLoadSuccess(long loadTime) {
		loadSuccessCount.increment();
		totalLoadTime.add(loadTime);
	}

	/**
	 * 새 항목의 실패한 로드를 기록한다. 캐시 요청으로 인해 항목이 로드되지만(예: {@link cache.Cache#get}),
	 * 항목을 로드하는 동안 예외가 발생하거나 null을 반환하는 경우 이 메소드를 호출해야 한다.
	 * {@link #recordMiss}와는 로드 스레드에서만 호출된다.
	 * @param loadTime 값이 없거나 예외가 발생하기 전에 캐시가 새 값을 계산하거나 검색하는 데 걸린 시간(나노초)
	 */
	public void recordLoadFailure(long loadTime) {
		loadFailureCount.increment();
		totalLoadTime.add(loadTime);
	}

	/**
	 * 캐시에서 항목 제거를 기록한다.
	 * 이는 캐시 제거 전략으로 인해 항목이 제거된 경우에만 호출되어야 한다.
	 */
	public void recordEviction() {
		evictionCount.increment();
	}

	/**
	 * 이 카운터 값의 스냅샷을 반환한다.
	 * @return 이 카운터 값의 스냅샷
	 */
	public CacheStats snapshot() {
		return CacheStats.of(
			negativeToMaxValue(hitCount.sum()),
			negativeToMaxValue(missCount.sum()),
			negativeToMaxValue(loadSuccessCount.sum()),
			negativeToMaxValue(loadFailureCount.sum()),
			negativeToMaxValue(totalLoadTime.sum()),
			negativeToMaxValue(evictionCount.sum())
		);
	}

	/**
	 * 음수가 아닌 경우 {@code value}를 반환한다.
	 * 그렇지 않으면 {@link Long#MAX_VALUE}를 반환한다.
	 * @param value 변환할 값
	 * @return {@code value} 혹은 음수의 경우 {@link Long#MAX_VALUE}로 변환된 값
	 */
	private static long negativeToMaxValue(long value) {
		return (value >= 0) ? value : Long.MAX_VALUE;
	}

	/**
	 * {@code other}의 값만큼 모든 카운터를 증가시킨다.
	 * @param other 증가할 카운터
	 */
	public void incrementBy(StatsCounter other) {
		CacheStats otherStats = other.snapshot();
		hitCount.add(otherStats.getHitCount());
		missCount.add(otherStats.getMissCount());
		loadSuccessCount.add(otherStats.getLoadSuccessCount());
		loadFailureCount.add(otherStats.getLoadFailureCount());
		totalLoadTime.add(otherStats.getTotalLoadTime());
		evictionCount.add(otherStats.getEvictionCount());
	}

	@Override
	public String toString() {
		return snapshot().toString();
	}
}