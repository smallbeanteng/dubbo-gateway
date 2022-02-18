package com.atommiddleware.cloud.core.serialize;

import org.springframework.lang.NonNull;

public interface Serialization {

	String serialize(Object object);
	
	byte[] serializeByte(Object object);
	
	<T> T deserialize(@NonNull String input, Class<T> clazz);
	
	<T> T convertValue(Object obj,Class<T> clazz);
	
	String serialize(Object object,boolean ignoreXss);
	
	<T> T deserialize(@NonNull String input, Class<T> clazz,boolean ignoreXss);
	
	<T> T convertValue(Object obj,Class<T> clazz,boolean ignoreXss);
}
