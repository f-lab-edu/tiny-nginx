import proxy.ProxyServerBootstrap;
import utils.BootstrapUtil;

public class ProxyServerLauncher {
	public static final String DEFAULT_CONFIGURATION_PATH = "core/src/main/resources/bootstrap.yml";

	public static void main(String[] args) {
		String configuationPath = System.getProperty("config.path", DEFAULT_CONFIGURATION_PATH);
		ProxyServerBootstrap proxyServerBootstrap = new ProxyServerBootstrap(BootstrapUtil.loadConfiguration(configuationPath));
		proxyServerBootstrap.start();
	}
}
