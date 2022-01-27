public class BootstrapConfiguration {
	private final int workerCount;

	public BootstrapConfiguration(int workerCount) {
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
		public BootstrapConfiguration build(){
			return new BootstrapConfiguration(workerCount);
		}
	}
}
