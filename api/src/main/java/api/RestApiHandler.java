package api;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
		Mono<Parameter> body = findById(id);
		return ok().contentType(MediaType.TEXT_EVENT_STREAM).body(body, Parameter.class);
	}

	private Mono<Parameter> findById(String id) {
		return databaseClient.execute("SELECT * FROM parameter WHERE id=:id")
			.bind("id", id)
			.as(Parameter.class)
			.fetch()
			.one();
	}

	public Mono<ServerResponse> save(ServerRequest request) {
		return request.bodyToMono(Parameter.class)
			.flatMap(data -> databaseClient.execute("INSERT INTO parameter (data) VALUES (:data)")
				.bind("data", data.getData())
				.fetch()
				.rowsUpdated()
				.doOnSuccess(s -> log.info("save success ==> count : {}", s.toString()))
				.doOnError(err -> log.error("save fail ==> {}", err.getMessage())))
			.flatMap(parameter -> ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(parameter)));
	}

	public Mono<ServerResponse> update(ServerRequest request) {
		Mono<Parameter> exist = findById(request.pathVariable("id"));
		return Mono.zip(
				data -> {
					Parameter p = (Parameter)data[0];
					Parameter p2 = (Parameter)data[1];

					if (p2 != null && StringUtils.hasText(p2.getData())) {
						p.setData(p2.getData());
					}

					return p;
				},
				exist,
				request.bodyToMono(Parameter.class)
			)
			.cast(Parameter.class)
			.flatMap(p -> databaseClient.execute("UPDATE parameter SET data=:data WHERE id=:id")
				.bind("id", p.getId())
				.bind("data", p.getData())
				.fetch()
				.rowsUpdated()
				.doOnSuccess(s -> log.info("update success ==> count : {}", s.toString()))
				.doOnError(err -> log.error("update error ==> {}", err.getMessage()))
				)
			.flatMap(body -> ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(body)));
			// .flatMap(body -> noContent().build());
	}

	public Mono<ServerResponse> delete(ServerRequest request) {
		String id = request.pathVariable("id");
		return databaseClient.execute("DELETE FROM parameter WHERE id=:id")
			.bind("id", id)
			.as(Parameter.class)
			.fetch()
			.rowsUpdated()
			.doOnSuccess(s -> log.info("delete success ==> count: {}", s.toString()))
			.doOnError(err -> log.error("delete error ==> {}", err.getMessage()))
			.flatMap(deleted -> noContent().build());
	}
}
