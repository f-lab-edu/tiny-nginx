public class ProxyBootstrapConfig {
	private final int workerCount;

	public ProxyBootstrapConfig(int workerCount) {
		this.workerCount = workerCount;
	}

	public int getWorkerCount() {
		return workerCount;
	}

	public static class Builder {
		private int workerCount;

		public Builder setWorkerCount(int workerCount) {
			this.workerCount = workerCount;
			return this;
		}
		public ProxyBootstrapConfig build(){
			return new ProxyBootstrapConfig(workerCount);
		}
	}
}
