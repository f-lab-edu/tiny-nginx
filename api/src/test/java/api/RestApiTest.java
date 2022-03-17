package api;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(RestApi.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void beforeEach() {
		objectMapper = Jackson2ObjectMapperBuilder.json()
			.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.modules(new JavaTimeModule())
			.build();
	}

	@Test
	@Order(1)
	void get() throws Exception {
		String url = "/api";

		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string(
				"[{\"id\":\"id0\",\"data\":\"data0\"},{\"id\":\"id1\",\"data\":\"data1\"},{\"id\":\"id2\",\"data\":\"data2\"},{\"id\":\"id3\",\"data\":\"data3\"},{\"id\":\"id4\",\"data\":\"data4\"},{\"id\":\"id5\",\"data\":\"data5\"},{\"id\":\"id6\",\"data\":\"data6\"},{\"id\":\"id7\",\"data\":\"data7\"},{\"id\":\"id8\",\"data\":\"data8\"},{\"id\":\"id9\",\"data\":\"data9\"}]"))
			.andExpect(content().json(
				"[{\"id\":\"id0\",\"data\":\"data0\"},{\"id\":\"id1\",\"data\":\"data1\"},{\"id\":\"id2\",\"data\":\"data2\"},{\"id\":\"id3\",\"data\":\"data3\"},{\"id\":\"id4\",\"data\":\"data4\"},{\"id\":\"id5\",\"data\":\"data5\"},{\"id\":\"id6\",\"data\":\"data6\"},{\"id\":\"id7\",\"data\":\"data7\"},{\"id\":\"id8\",\"data\":\"data8\"},{\"id\":\"id9\",\"data\":\"data9\"}]"))
			.andDo(print());
	}

	@Test
	@Order(2)
	void post() throws Exception {
		String url = "/api/id123";

		mockMvc.perform(MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string(
				"[{\"id\":\"id0\",\"data\":\"data0\"},{\"id\":\"id1\",\"data\":\"data1\"},{\"id\":\"id2\",\"data\":\"data2\"},{\"id\":\"id3\",\"data\":\"data3\"},{\"id\":\"id4\",\"data\":\"data4\"},{\"id\":\"id5\",\"data\":\"data5\"},{\"id\":\"id6\",\"data\":\"data6\"},{\"id\":\"id7\",\"data\":\"data7\"},{\"id\":\"id8\",\"data\":\"data8\"},{\"id\":\"id9\",\"data\":\"data9\"},{\"id\":\"id123\",\"data\":\"id123\"}]"))
			.andExpect(content().json(
				"[{\"id\":\"id0\",\"data\":\"data0\"},{\"id\":\"id1\",\"data\":\"data1\"},{\"id\":\"id2\",\"data\":\"data2\"},{\"id\":\"id3\",\"data\":\"data3\"},{\"id\":\"id4\",\"data\":\"data4\"},{\"id\":\"id5\",\"data\":\"data5\"},{\"id\":\"id6\",\"data\":\"data6\"},{\"id\":\"id7\",\"data\":\"data7\"},{\"id\":\"id8\",\"data\":\"data8\"},{\"id\":\"id9\",\"data\":\"data9\"},{\"id\":\"id123\",\"data\":\"id123\"}]"))
			.andDo(print());
	}

	@Test
	@Order(3)
	void put() throws Exception {
		String url = "/api/id0";

		Parameter parameter = new Parameter();
		parameter.setId("id0");
		parameter.setData("testData");
		String content = objectMapper.writeValueAsString(parameter);

		mockMvc.perform(MockMvcRequestBuilders.put(url)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string(
				"[{\"id\":\"id0\",\"data\":\"testData\"},{\"id\":\"id1\",\"data\":\"data1\"},{\"id\":\"id2\",\"data\":\"data2\"},{\"id\":\"id3\",\"data\":\"data3\"},{\"id\":\"id4\",\"data\":\"data4\"},{\"id\":\"id5\",\"data\":\"data5\"},{\"id\":\"id6\",\"data\":\"data6\"},{\"id\":\"id7\",\"data\":\"data7\"},{\"id\":\"id8\",\"data\":\"data8\"},{\"id\":\"id9\",\"data\":\"data9\"},{\"id\":\"id123\",\"data\":\"id123\"}]"))
			.andExpect(content().json(
				"[{\"id\":\"id0\",\"data\":\"testData\"},{\"id\":\"id1\",\"data\":\"data1\"},{\"id\":\"id2\",\"data\":\"data2\"},{\"id\":\"id3\",\"data\":\"data3\"},{\"id\":\"id4\",\"data\":\"data4\"},{\"id\":\"id5\",\"data\":\"data5\"},{\"id\":\"id6\",\"data\":\"data6\"},{\"id\":\"id7\",\"data\":\"data7\"},{\"id\":\"id8\",\"data\":\"data8\"},{\"id\":\"id9\",\"data\":\"data9\"},{\"id\":\"id123\",\"data\":\"id123\"}]"))
			.andDo(print());
	}

	@Test
	@Order(4)
	void delete() throws Exception {
		String url = "/api/id0";

		mockMvc.perform(MockMvcRequestBuilders.delete(url)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().string(
				"[{\"id\":\"id1\",\"data\":\"data1\"},{\"id\":\"id2\",\"data\":\"data2\"},{\"id\":\"id3\",\"data\":\"data3\"},{\"id\":\"id4\",\"data\":\"data4\"},{\"id\":\"id5\",\"data\":\"data5\"},{\"id\":\"id6\",\"data\":\"data6\"},{\"id\":\"id7\",\"data\":\"data7\"},{\"id\":\"id8\",\"data\":\"data8\"},{\"id\":\"id9\",\"data\":\"data9\"},{\"id\":\"id123\",\"data\":\"id123\"}]"))
			.andExpect(content().json(
				"[{\"id\":\"id1\",\"data\":\"data1\"},{\"id\":\"id2\",\"data\":\"data2\"},{\"id\":\"id3\",\"data\":\"data3\"},{\"id\":\"id4\",\"data\":\"data4\"},{\"id\":\"id5\",\"data\":\"data5\"},{\"id\":\"id6\",\"data\":\"data6\"},{\"id\":\"id7\",\"data\":\"data7\"},{\"id\":\"id8\",\"data\":\"data8\"},{\"id\":\"id9\",\"data\":\"data9\"},{\"id\":\"id123\",\"data\":\"id123\"}]"))
			.andDo(print());
	}
}