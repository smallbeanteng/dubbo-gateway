package com.atommiddleware.cloud.core.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import com.atommiddleware.cloud.core.serialize.Serialization;
import com.atommiddleware.cloud.security.cas.PrincipalObtain;
@SuppressWarnings("unchecked")
public class DefaultPrincipalObtain implements PrincipalObtain {

	@Autowired
	private Serialization serialization;

	public final List<String> principalAttrs;

	public DefaultPrincipalObtain(Serialization serialization, List<String> principalAttrs) {
		this.principalAttrs = principalAttrs;
	}

	@Override
	public Map<String, String> getPrincipal() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if (null != securityContext) {
			Authentication authentication = securityContext.getAuthentication();
			if (null != authentication && !CollectionUtils.isEmpty(principalAttrs)) {
				Map<String, String> mapPrincipal = serialization.convertValue(authentication.getPrincipal(), Map.class,
						true);
				if (!CollectionUtils.isEmpty(mapPrincipal)) {
					Map<String, String> mapResult = new HashMap<String, String>();
					Object attr = null;
					for (String principalAttrKey : principalAttrs) {
						attr = mapPrincipal.get(principalAttrKey);
						if (null!=attr) {
							mapResult.put(principalAttrKey, String.valueOf(attr));
						}
					}
					return mapResult;
				}
			}
		}
		return null;
	}

}
