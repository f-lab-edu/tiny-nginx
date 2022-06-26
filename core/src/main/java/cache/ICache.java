package cache;

/**
 * 키-값을 반영구적으로 매핑한다.
 * 캐시 항목은 {@link #get(Object)} 또는 {@link #put(Object, Object)}을 사용하여 수동으로 추가되며,
 * 제거되거나 수동으로 무효화될 때까지 캐시에 저장된다.
 * <p>이 인터페이스의 구현은 thread-safe할 것으로 기대된다.</p>
 * @param <K> 캐시에서 유지 관리하는 키 유형
 * @param <V> 매핑된 값의 유형
 */
public interface ICache<K, V> {
	/**
	 * 검색할 캐시의 {@code key}와 관련된 값을 반환한다.
	 * @param key 지정된 값이 연결될 키 키
	 * @return 지정된 키와 관련된 현재 값, 없을 경우 null
	 */
	V get(K key);

	/**
	 * {@code value}를 이 캐시의 {@code key}와 연결한다.
	 * 캐시가 이전에 {@code key}와 관련된 값이 포함되어 있으면, 이전 값이 새 {@code value}로 대체된다.
	 * @param key 지정된 값과 연결할 키
	 * @param value 지정된 키와 연결할 값
	 */
	void put(K key, V value);

	/**
	 * 캐시에 필요한 정리 작업을 수행한다.
	 * 정확히 어떤 작업을 수행하는지는 구현에 따라 달라진다.
	 */
	void cleanUp();

	/**
	 * {@code key}에 대해 캐시된 모든 값을 삭제한다.
	 * @param key 캐시에서 매핑을 제거할 키
	 */
	void remove(K key);

	/**
	 * 이 캐시에 있는 모든 항목의 수를 반환한다.
	 * @return 캐시의 모든 항목의 수
	 */
	int size();
}
