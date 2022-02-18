package com.atommiddleware.cloud.core.filter;

import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;

import com.atommiddleware.cloud.core.annotation.ResponseZuulServletResult;
import com.netflix.zuul.context.RequestContext;

public class ZuulErrorFilter extends SendErrorFilter {

	private final ResponseZuulServletResult responseZuulServletResult;

	public ZuulErrorFilter(ResponseZuulServletResult responseZuulServletResult) {
		this.responseZuulServletResult = responseZuulServletResult;
	}

	@Override
	public int filterOrder() {
		return super.filterOrder() - 1;
	}

	@Override
	public Object run() {
		try {
			RequestContext ctx = RequestContext.getCurrentContext();
			ExceptionHolder exception = findZuulException(ctx.getThrowable());
			ctx.remove("throwable");
			ctx.set("SEND_ERROR_FILTER_RAN", true);
			HttpStatus httpStatus = HttpStatus.valueOf(exception.getStatusCode());
			if (null != httpStatus) {
				responseZuulServletResult.sevletZuulResponseException(httpStatus, null);
			} else {
				responseZuulServletResult.sevletZuulResponseException(HttpStatus.INTERNAL_SERVER_ERROR, null);
			}
		} catch (Exception ex) {
			ReflectionUtils.rethrowRuntimeException(ex);
		}
		return null;
	}
}
