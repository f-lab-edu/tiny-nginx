import java.util.List;

public class ProxyBootstrapConfig {
	private Worker worker;
	private Server server;
	private Cache cache;

	public Server getServer() {
		return server;
	}

	public Worker getWorker() {
		return worker;
	}

	public Cache getCache() {
		return cache;
	}

	public class Worker{
		private int count;

		public int getCount() {
			return count;
		}

		@Override
		public String toString() {
			return "Worker{" +
				"count=" + count +
				'}';
		}
	}

	public class Server{
		private int listen;
		private String name;
		private List<String> upstream;

		public int getListen() {
			return listen;
		}

		public String getName() {
			return name;
		}

		public List<String> getUpstream() {
			return upstream;
		}

		@Override
		public String toString() {
			return "Server{" +
				"listen=" + listen +
				", name='" + name + '\'' +
				", upstream=" + upstream +
				'}';
		}
	}

	public class Cache {
		private String path;

		public String getPath() {
			return path;
		}

		@Override
		public String toString() {
			return "Cache{" +
				"path='" + path + '\'' +
				'}';
		}
	}

	@Override
	public String toString() {
		return "ProxyBootstrapConfig{" +
			"worker=" + worker +
			", server=" + server +
			", cache=" + cache +
			'}';
	}
}
