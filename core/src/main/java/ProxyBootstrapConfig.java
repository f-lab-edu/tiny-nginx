import java.util.List;

import utils.YamlUtil;

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

	}

	public class Cache {
		private String path;

		public String getPath() {
			return path;
		}

	}

	@Override
	public String toString() {
		return YamlUtil.toString(this);
	}
}
