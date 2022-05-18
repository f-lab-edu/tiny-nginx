package proxy;

import java.util.List;
import java.util.Map;

import utils.YamlUtil;

public class ProxyBootstrapConfig {
	private int acceptorCount;
	private int workerCount;
	private int listen;
	private String serverName;
	private Map<String, List<String>> locations;
	private String cacheDirectoryPath;


	public int getAcceptorCount() {
		return acceptorCount;
	}

	public int getWorkerCount() {
		return workerCount;
	}

	public int getListen() {
		return listen;
	}

	public String getServerName() {
		return serverName;
	}

	public Map<String, List<String>> getLocations() {
		return locations;
	}

	public String getCacheDirectoryPath() {
		return cacheDirectoryPath;
	}

	@Override
	public String toString() {
		return YamlUtil.toString(this);
	}
}
