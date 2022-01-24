package com.atommiddleware.cloud.core.annotation;

import java.util.Set;

public interface BaseApiWrapper {

	Set<String> getPathPatterns();

	public enum ParamFromType {
		FROM_BODY(1), FROM_COOKIE(2), FROM_HEADER(3), FROM_PATH(5), FROM_ATTRIBUTE(6), FROM_QUERYPARAMS(7);

		private int paramFromType;

		ParamFromType(int paramFromType) {
			this.paramFromType = paramFromType;
		}

		public int getParamFromType() {
			return paramFromType;
		}
	}
}
