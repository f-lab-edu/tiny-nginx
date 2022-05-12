package api;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import api.config.R2dbcConfiguration;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RestApiHandler {

	private final R2dbcConfiguration r2dbcConfiguration;
	private final DatabaseClient databaseClient;
	private static final Logger logger = LoggerFactory.getLogger(RestApiHandler.class);

	public RestApiHandler(R2dbcConfiguration r2dbcConfiguration,
		DatabaseClient databaseClient) {
		this.r2dbcConfiguration = r2dbcConfiguration;
		this.databaseClient = r2dbcConfiguration.r2dbcEntityTemplate().getDatabaseClient();
	}

	public Mono<ServerResponse> findAll(ServerRequest request) {
		Flux<Parameter> parameter = databaseClient.execute("SELECT * FROM parameter")
			.as(Parameter.class)
			.fetch()
			.all();
		return ok().contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromProducer(parameter, Parameter.class));
	}

	public Mono<ServerResponse> findById(ServerRequest request) {
		String id = request.pathVariable("id");
		Mono<Parameter> body = databaseClient.execute("SELECT * FROM parameter WHERE id=:id")
			.bind("id", id)
			.as(Parameter.class)
			.fetch()
			.one();
		return ok().contentType(MediaType.TEXT_EVENT_STREAM).body(body, Parameter.class);
	}

	public Mono<ServerResponse> save(ServerRequest request) {
		return request.bodyToMono(Parameter.class)
			.flatMap(data -> databaseClient.execute("INSERT INTO parameter (data) VALUES (:data)")
				.bind("data", data.getData())
				.fetch()
				.first()
				.doOnSuccess(s -> log.info("success ==> {}", s))
				.doOnError(err -> log.error("error ==> {}", err.toString()))
				.map(r -> (Long)r.get("id")))
			.flatMap(save -> noContent().build());
	}

	// public Mono<ServerResponse> update(ServerRequest request) {
	//
	// }

	public Mono<ServerResponse> delete(ServerRequest request) {
		String id = request.pathVariable("id");
		return databaseClient.execute("DELETE FROM parameter WHERE id=:id")
			.bind("id", id)
			.as(Parameter.class)
			.fetch()
			.rowsUpdated()
			.doOnSuccess(s -> log.info("success ==> {}", s))
			.doOnError(err -> log.error("error ==> {}", err.toString()))
			.flatMap(deleted -> noContent().build());
	}
}
