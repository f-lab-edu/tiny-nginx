package api;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@EnableAutoConfiguration
public class RestApi {
	private static final Logger logger = LoggerFactory.getLogger(RestApi.class);
	ArrayList<Parameter> parameters = new ArrayList<>();

	public RestApi() {
		setParam();
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	ResponseEntity<?> get() {
		return ResponseEntity.ok(parameters);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	ResponseEntity<?> post(@PathVariable("id") String id) {
		Parameter parameter = new Parameter();
		parameter.setId(id);
		parameter.setData(id);
		parameters.add(parameter);

		return ResponseEntity.ok(parameters);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	ResponseEntity<?> put(@RequestBody Parameter paramData) {
		for (Parameter param : parameters) {
			if (param.getId().equals(paramData.getId())) {
				param.setData(paramData.getData());
			}
		}
		return ResponseEntity.ok(parameters);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	ResponseEntity<?> delete(@PathVariable String id) {
		Parameter parameter = new Parameter();

		for (Parameter param : parameters) {
			if (param.getId().equals(id)) {
				parameter.setId(id);
				parameter.setData(param.getData());
			}
		}

		parameters.remove(parameter);
		return ResponseEntity.ok(parameters);
	}

	void setParam() {
		for (int i = 0; i < 10; i++) {
			Parameter parameter = new Parameter();
			parameter.setId("id" + i);
			parameter.setData("data" + i);
			parameters.add(parameter);
		}
	}
}
