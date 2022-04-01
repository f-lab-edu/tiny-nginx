package api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@EnableAutoConfiguration
public class RestApiController {
	private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	private RestApiService restApiService;

	@Autowired
	public RestApiController(RestApiService restApiService) {
		this.restApiService = restApiService;
	}

	@GetMapping("")
	public List<ParameterDto> getList() {
		return restApiService.getAllList();
	}

	@GetMapping("/{id}")
	public ParameterDto getListById(@PathVariable("id") String id) {
		return restApiService.getListById(id);
	}

	@PostMapping("")
	public ParameterDto save(@RequestBody ParameterDto parameterDto) {
		return restApiService.save(parameterDto);
	}

	@PutMapping("/{id}")
	public ParameterDto update(@PathVariable("id") String id, @RequestBody ParameterDto parameterDto) {
		return restApiService.update(id, parameterDto);
	}

	@DeleteMapping("/{id}")
	public boolean deleteById(@PathVariable("id") String id) {
		return restApiService.deleteById(id);
	}
}
