package integration.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class SimpleSimulation extends Simulation {
	HttpProtocolBuilder httpProtocol = http.baseUrl("https://stackoverflow.com/")
		.acceptHeader("application/json")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,id;q=0.6,th;q=0.5,zh-TW;q=0.4,zh;q=0.3")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36");

	ScenarioBuilder scenario = scenario("Scenario").exec(http("request-${currentTimeMillis()}").get("/questions/64310023/why-am-i-getting-this-httprequestaction-httprequest-1-failed-to-execute-no"));

	{
		setUp(scenario.injectOpen(atOnceUsers(1)).protocols(httpProtocol));
	}
}

