package com.atommiddleware.sample.web.provider.order;

import org.apache.dubbo.config.annotation.DubboService;

import com.atommiddleware.cloud.sample.api.Result;
import com.atommiddleware.cloud.sample.api.order.OrderService;
@DubboService
public class OrderServiceImpl implements OrderService{

	@Override
	public Result placeOrder(String orderCode) {
		return Result.from().setData("notice", "success place order");
	}
}
