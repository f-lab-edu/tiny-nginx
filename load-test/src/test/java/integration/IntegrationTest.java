package integration;

import java.io.IOException;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;

public class IntegrationTest {
	public static void main(String[] args) throws IOException {
		GatlingPropertiesBuilder gatlingPropertiesBuilder = new GatlingPropertiesBuilder()
			.simulationClass("integration.simulations.SimpleSimulation")
			.resourcesDirectory("src/test/resources")
			.resultsDirectory("results/report")
			.noReports();

		Gatling.fromMap(gatlingPropertiesBuilder.build());
	}
}
