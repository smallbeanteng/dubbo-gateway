package com.atommiddleware.cloud.core.serialize;

import java.io.IOException;

import com.atommiddleware.cloud.core.security.XssSecurity;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class CustomXssObjectMapper extends ObjectMapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final XssSecurity xssSecurity;

	public CustomXssObjectMapper(XssSecurity xssSecurity) {
		this.xssSecurity = xssSecurity;
		SimpleModule module = new SimpleModule("HTML XSS Serializer",
				new Version(1, 0, 0, "FINAL", "com.atommiddleware", "ep-jsonmodule"));
		module.addSerializer(new JsonHtmlXssSerializer(String.class));
		module.addDeserializer(String.class, new JsonHtmlXssDeserializer(String.class));
		this.registerModule(module);
	}

	class JsonHtmlXssSerializer extends JsonSerializer<String> {

		public JsonHtmlXssSerializer(Class<String> string) {
			super();
		}

		public Class<String> handledType() {
			return String.class;
		}

		@Override
		public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			String encodedValue = xssSecurity.xssClean(value);
			if (null != encodedValue) {
				gen.writeString(encodedValue);
			}
		}
	}

	class JsonHtmlXssDeserializer extends JsonDeserializer<String> {

		public JsonHtmlXssDeserializer(Class<String> string) {
			super();
		}

		@Override
		public Class<String> handledType() {
			return String.class;
		}

		@Override
		public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
				throws IOException, JsonProcessingException {
			String value = jsonParser.getValueAsString();
			if (value != null) {
				return xssSecurity.xssClean(value);
			}
			return value;
		}
	}

}
