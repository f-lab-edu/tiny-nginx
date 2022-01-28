
import java.io.File;

import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	public BootstrapConfiguration configuration() {
		String configuationPath = System.getProperty("bootstrap.config.path", "bootstrap.yml");
		try {
			ImmutableHierarchicalConfiguration configuration = new FileBasedConfigurationBuilder<>(
				YAMLConfiguration.class)
				.configure(new Parameters()
					.fileBased()
					.setFile(new File(configuationPath)))
				.getConfiguration();

			return new BootstrapConfiguration.Builder()
				.setWorkerCount(configuration.get(Integer.class, "worker.count"))
				.build();

		} catch (ConfigurationException exception) {
			throw new IllegalArgumentException(String.format("couldn't load configuration from %s", configuationPath),
				exception);
		}
	}
}
