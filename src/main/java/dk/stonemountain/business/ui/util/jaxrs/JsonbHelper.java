package dk.stonemountain.business.ui.util.jaxrs;

import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

public class JsonbHelper {
	private static final Logger logger = LoggerFactory.getLogger(JsonbHelper.class);
	
	private JsonbHelper() {
		// to prevent instantiation
	}
	
	public static String toJson(Object data) {
		try {
			String content = "";
			JsonbConfig config = new JsonbConfig().withPropertyVisibilityStrategy(new PrivateVisibilityStrategy());
			try (Jsonb jsonb = JsonbBuilder.create(config)) {
				content = jsonb.toJson(data);
			}
			return content;
		} catch (Exception e) {
			throw new RuntimeException("Can not generate json", e);
		}
	}

	public static <T> T fromJson(String content, Class<T> responseClass) {
		return fromJson(content, responseClass, false);
	}

	public static <T> T fromJson(String content, Class<T> responseClass, boolean noLogging) {
		if (!noLogging) {
			logger.debug("Unmarshalling : {}", content);
		}
		JsonbConfig config = new JsonbConfig().withPropertyVisibilityStrategy(new PrivateVisibilityStrategy());
		try (Jsonb jsonb = JsonbBuilder.create(config)) {
			return jsonb.fromJson(content, responseClass);
		} catch (Exception e) {
			throw new RuntimeException("Failed to handle json", e);
		}
	}
	
	public static <T> List<T> fromJson(String content, Type runtimeType, boolean noLogging) {
		if (!noLogging) {
			logger.trace("Unmarshalling : {}", content);
		}
		JsonbConfig config = new JsonbConfig().withPropertyVisibilityStrategy(new PrivateVisibilityStrategy());
		try (Jsonb jsonb = JsonbBuilder.create(config)) {
			return jsonb.fromJson(content, runtimeType);
		} catch (Exception e) {
			throw new RuntimeException("Failed to handle json", e);
		}
	}

	public static <T> List<T> fromJson(String content, Type runtimeType) {
		return fromJson(content, runtimeType, false);
	}
}