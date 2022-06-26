package api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.h2.H2ConnectionOption;
import io.r2dbc.spi.ConnectionFactory;

@Configuration
@EnableR2dbcRepositories
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {
	@Override
	public ConnectionFactory connectionFactory() {
		return new H2ConnectionFactory(H2ConnectionConfiguration.builder()
			.inMemory("test")
			.property(H2ConnectionOption.DB_CLOSE_DELAY, "-1")
			.property(H2ConnectionOption.DB_CLOSE_ON_EXIT, "FALSE")
			.build());
	}

	@Bean
	public ConnectionFactoryInitializer initializer() {
		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory());

		CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
		populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
		populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));
		initializer.setDatabasePopulator(populator);

		return initializer;
	}

	@Bean
	public R2dbcEntityTemplate r2dbcEntityTemplate() {
		DatabaseClient databaseClient = DatabaseClient.create(connectionFactory());
		return new R2dbcEntityTemplate(databaseClient);
	}
}
