package api;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import api.config.R2dbcConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestApiHandler {
	private final R2dbcConfiguration r2dbcConfiguration;

	private static final Logger logger = LoggerFactory.getLogger(RestApiHandler.class);

	public Mono<ServerResponse> findAll(ServerRequest request) {
		Flux<Parameter> parameter = r2dbcConfiguration.r2dbcEntityTemplate()
			.getDatabaseClient()
			.execute("select * from parameter")
			.as(Parameter.class)
			.fetch()
			.all();

		return ok().contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromProducer(parameter, Parameter.class));
	}

	public Mono<ServerResponse> findById(ServerRequest request) {
		String id = request.pathVariable("id");

		Mono<Parameter> body = r2dbcConfiguration.r2dbcEntityTemplate()
			.getDatabaseClient()
			.execute("select * from parameter where id=" + id)
			.as(Parameter.class)
			.fetch().one();
		return ok().contentType(MediaType.TEXT_EVENT_STREAM).body(body, Parameter.class);
	}
}
