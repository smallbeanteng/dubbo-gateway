package com.atommiddleware.sample.web.provider.order;

import org.apache.dubbo.config.annotation.DubboService;

import com.atommiddleware.cloud.sample.api.Result;
import com.atommiddleware.cloud.sample.api.order.OrderQuery;

@DubboService
public class OrderQueryImpl implements OrderQuery{
	@Override
	public Result getOrder(String orderCode) {
		return Result.from().setData("orderCode", orderCode);
	}

}
