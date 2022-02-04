package cache;

public enum CacheStatus {
	MISS, BYPASS, EXPIRED, STALE, UPDATING, REVALIDATE, HIT;

	public boolean isMiss() {
		return this == MISS;
	}

	public boolean isBypass() {
		return this == BYPASS;
	}

	public boolean isExpired() {
		return this == EXPIRED;
	}

	public boolean isStale() {
		return this == STALE;
	}

	public boolean isUpdating() {
		return this == UPDATING;
	}

	public boolean isRevalidate() {
		return this == REVALIDATE;
	}

	public boolean isHit() {
		return this == HIT;
	}
}
