package com.atommiddleware.cloud.core.annotation;

import java.io.IOException;
import org.springframework.boot.WebApplicationType;
import com.atommiddleware.cloud.core.config.DubboReferenceConfigProperties;
import javassist.CannotCompileException;
import javassist.NotFoundException;

public interface DubboApiWrapperFactory {

	public  Class<?> make(String id, Class<?> interfaceClass,
			DubboReferenceConfigProperties dubboReferenceConfigProperties, WebApplicationType webApplicationType)
			throws CannotCompileException, NotFoundException, IllegalArgumentException, IllegalAccessException,
			IOException;
}
