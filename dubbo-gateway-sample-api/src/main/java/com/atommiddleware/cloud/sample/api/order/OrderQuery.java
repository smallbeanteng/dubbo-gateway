package com.atommiddleware.cloud.sample.api.order;

import com.atommiddleware.cloud.api.annotation.FromQueryParams;
import com.atommiddleware.cloud.api.annotation.GateWayDubbo;
import com.atommiddleware.cloud.api.annotation.PathMapping;
import com.atommiddleware.cloud.api.annotation.PathMapping.RequestMethod;
import com.atommiddleware.cloud.sample.api.Result;

@GateWayDubbo("orderQuery")
public interface OrderQuery {
	@PathMapping(value = "/order/getOrder",requestMethod = RequestMethod.GET)
	Result getOrder(@FromQueryParams("orderCode") String orderCode);
}
