package webflux;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

import webflux.config.WebConfiguration;

@SpringJUnitConfig(WebConfiguration.class)
class RestApiRouterTest {
	private WebTestClient client;

	@BeforeEach
	void setUp(ApplicationContext context) {
		client = WebTestClient
			.bindToApplicationContext(context)
			.build();
	}

	@Test
	void save_data() {
		client.post()
			.uri("/domain1/api")
			.bodyValue(new Parameter("test"))
			.exchange()
			.expectBody(String.class)
			.returnResult();
	}

	@Test
	void save_no_data() {
		client.post()
			.uri("/domain1/api")
			.bodyValue(new Parameter())
			.exchange()
			.expectStatus().is4xxClientError();
	}

	@Test
	void update_data() {
		client.put()
			.uri("/domain1/api/1")
			.bodyValue(new Parameter(1L, "123"))
			.exchange()
			.expectBody(String.class)
			.returnResult();
	}

	@Test
	void delete_id_1() {
		client.delete()
			.uri("/domain1/api/1")
			.exchange()
			.expectStatus().isNotFound()
			.expectBody().isEmpty();
	}
}