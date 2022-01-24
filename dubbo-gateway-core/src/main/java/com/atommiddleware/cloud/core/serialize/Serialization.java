package com.atommiddleware.cloud.core.serialize;

import org.springframework.lang.NonNull;

public interface Serialization {

	String serialize(Object object);
	
	<T> T deserialize(@NonNull String input, Class<T> clazz);
}
