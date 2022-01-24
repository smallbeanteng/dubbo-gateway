package com.atommiddleware.cloud.sample.api.order;

import com.atommiddleware.cloud.api.annotation.FromBody;
import com.atommiddleware.cloud.api.annotation.GateWayDubbo;
import com.atommiddleware.cloud.api.annotation.PathMapping;
import com.atommiddleware.cloud.sample.api.Result;

@GateWayDubbo("orderService")
public interface OrderService {
	@PathMapping(path = "/order/placeOrder")
	Result placeOrder(@FromBody String orderCode);
}
