package webflux;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class RestApiRouter {
	private final RestApiHandler handler;

	@Value("${spring.webflux.base-path}")
	private String basePath;

	@Bean
	public RouterFunction<ServerResponse> routes() {
		return route().path(basePath, builder -> builder
			.nest(accept(MediaType.APPLICATION_JSON), b -> b
				.GET("/webflux", handler::findAll)
				.GET("/webflux/{id}", handler::findById))
				.POST("/webflux", handler::save)
				.PUT("/webflux/{id}", handler::update)
				.DELETE("/webflux/{id}", handler::delete))
			.build();
	}
}
