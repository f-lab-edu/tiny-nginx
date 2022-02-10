package cache;

public class MetaValue {
	private final byte[] data;
	private long expiryTime;

	public MetaValue(byte[] data) {
		this.data = data;
	}

	public MetaValue(byte[] data, long expiredTime) {
		this.data = data;
		this.expiryTime = expiredTime;
	}

	public byte[] getData() {
		return data;
	}

	public long getExpiryTime() {
		return expiryTime;
	}
}
