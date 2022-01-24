package com.atommiddleware.cloud.core.serialize;

import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonSerialization implements Serialization {

	private static ObjectMapper mapper = new ObjectMapper();
	static {
		// 对于空的对象转json的时候不抛出错误
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		// 允许属性名称没有引号
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 允许单引号
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 设置输入时忽略在json字符串中存在但在java对象实际没有的属性
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 设置输出时包含属性的风格
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@Override
	public String serialize(Object serializeObject) {
		if (serializeObject == null) {
			return null;
		}
		String json = null;
		try {
			json = mapper.writeValueAsString(serializeObject);
		} catch (JsonProcessingException e) {
			log.error(" toJsonString error", e);
		}
		return json;
	}

	@Override
	public <T> T deserialize(String input, Class<T> clazz) {
		T t = null;
		try {
			t = mapper.readValue(input, clazz);
		} catch (Exception e) {
			log.error(" parse json to class [{}] ", clazz.getSimpleName());
		}
		return t;
	}

	@Override
	public <T> T deserialize(InputStream input, Class<T> clazz) {
		T t = null;
		try {
			t = mapper.readValue(input, clazz);
		} catch (Exception e) {
			log.error(" parse json to class [{}] error", clazz.getSimpleName());
		}
		return t;
	}

	@Override
	public byte[] serializeByte(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return mapper.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			log.error(" toJsonString error", e);
		}
		return null;
	}

}
