package utils;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import utils.exception.YamlUtilExcaption;

public class YamlUtil {
	private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

	public static <T> T toObject(File sourcePath, Class<T> targetClass) {
		try {
			return objectMapper.readValue(sourcePath, targetClass);

		} catch (StreamReadException | DatabindException exception) {
			throw new YamlUtilExcaption(String.format("You can't convert file to target class. because of the exception : %s",exception.getMessage()), exception);
		} catch (IOException exception) {
			throw new YamlUtilExcaption(String.format("You can't load file from path. because of the exception : %s",exception.getMessage()), exception);
		}
	}

	public static <T> String toString(T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException exception) {
			throw new YamlUtilExcaption(String.format("You can't convert object to string. because of the exception : %s",exception.getMessage()), exception);
		}
	}
}
