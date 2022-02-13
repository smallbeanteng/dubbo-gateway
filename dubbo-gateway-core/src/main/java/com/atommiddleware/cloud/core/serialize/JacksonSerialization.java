package com.atommiddleware.cloud.core.serialize;

import com.atommiddleware.cloud.core.security.XssSecurity;
import com.atommiddleware.cloud.core.security.XssSecurity.XssFilterStrategy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonSerialization implements Serialization {

	private final ObjectMapper mapper;
	private final CustomXssObjectMapper customXssObjectMapper;
	private final boolean enableXssFilter;
	private final XssFilterStrategy xssFilterStrategy;

	public JacksonSerialization(boolean enableXssFilter, XssSecurity xssSecurity, XssFilterStrategy xssFilterStrategy) {
		this.enableXssFilter = enableXssFilter;
		this.xssFilterStrategy = xssFilterStrategy;
		customXssObjectMapper = new CustomXssObjectMapper(xssSecurity);
		initMapper(customXssObjectMapper);
		mapper = new ObjectMapper();
		initMapper(mapper);
		// 对于空的对象转json的时候不抛出错误

	}

	private void initMapper(ObjectMapper mapperTemp) {
		mapperTemp.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		// 允许属性名称没有引号
		mapperTemp.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		// 允许单引号
		mapperTemp.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 设置输入时忽略在json字符串中存在但在java对象实际没有的属性
		mapperTemp.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 设置输出时包含属性的风格
		mapperTemp.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		// 忽略大小写
		mapperTemp.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	}

	@Override
	public String serialize(Object value) {
		if (value == null) {
			return null;
		}
		try {
			if (enableXssFilter && xssFilterStrategy == XssFilterStrategy.RESPONSE) {
				// 响应要过滤xss
				return customXssObjectMapper.writeValueAsString(value);
			} else {
				return mapper.writeValueAsString(value);
			}
		} catch (JsonProcessingException e) {
			log.error(" toJsonString error", e);
		}
		return null;
	}

	@Override
	public byte[] serializeByte(Object value) {
		if (value == null) {
			return null;
		}
		try {
			if (enableXssFilter && xssFilterStrategy == XssFilterStrategy.RESPONSE) {
				// 响应要过滤xss
				return customXssObjectMapper.writeValueAsBytes(value);
			} else {
				return mapper.writeValueAsBytes(value);
			}
		} catch (JsonProcessingException e) {
			log.error(" toJsonByte error", e);
		}
		return null;
	}

	@Override
	public <T> T deserialize(String input, Class<T> clazz) {
		T t = null;
		try {
			if (enableXssFilter
					&&  xssFilterStrategy == XssFilterStrategy.REQUEST) {
				// 请求需要过滤xss
				t = customXssObjectMapper.readValue(input, clazz);
			} else {
				t = mapper.readValue(input, clazz);
			}
		} catch (Exception e) {
			log.error(" parse json to class [{}] ", clazz.getSimpleName());
		}
		return t;
	}

	@Override
	public <T> T convertValue(Object obj, Class<T> clazz) {
		T t = null;
		try {
			if (enableXssFilter
					&& xssFilterStrategy == XssFilterStrategy.REQUEST) {
				t = customXssObjectMapper.convertValue(obj, clazz);
			} else {
				t = mapper.convertValue(obj, clazz);
			}
		} catch (Exception e) {
			log.error(" parse json to class [{}] error", clazz.getSimpleName());
		}
		return t;
	}

}
