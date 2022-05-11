package api;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface RestApiRepository extends ReactiveCrudRepository<Parameter, Long> {
	@Query("SELECT * FROM parameter WHERE data = :data")
	Mono<Parameter> findByData(String data);
}
