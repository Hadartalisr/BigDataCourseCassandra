package bigdatacourse.hw2.studentcode.helpers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelpers {

	private ObjectMapper objectMapper;

	public JsonHelpers() {
		// add to allow single quote
		JsonFactory factory = new JsonFactory();
		factory.enable(Feature.ALLOW_SINGLE_QUOTES);
		objectMapper = new ObjectMapper(factory);
	}

	public <T> T deserialize(String jsonString, Class<T> clazz) throws IOException {
		return objectMapper.readValue(jsonString, clazz);
	}
}
