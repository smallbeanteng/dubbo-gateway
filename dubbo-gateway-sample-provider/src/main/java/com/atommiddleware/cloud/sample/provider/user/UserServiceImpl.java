package com.atommiddleware.cloud.sample.provider.user;

import org.apache.dubbo.config.annotation.DubboService;

import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.ThreadLocalRandom;
import com.atommiddleware.cloud.sample.api.Result;
import com.atommiddleware.cloud.sample.api.user.UserService;
import com.atommiddleware.cloud.sample.api.user.domain.User;

@DubboService
public class UserServiceImpl implements UserService {
	@Override
	public Result registerUser(User user) {
		int time=ThreadLocalRandom.current().nextInt(20, 150);
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(time%9==0) {
		System.out.println(user);
		}
		return Result.from().setData("user", user);
	}

	@Override
	public Result registerUserFromHeader(User user) {
		return Result.from().setData("user", user);
	}

	@Override
	public Result registerUserFromCookie(User user) {
		return Result.from().setData("user", user);
	}

	@Override
	public Result registerUserFromPath(User user) {
		return Result.from().setData("user", user);
	}

	@Override
	public Result unRegisterUser(Long userId) {
		return Result.from().setData("userId", userId);
	}

	@Override
	public Result getUserInfo(Long userId, Short gender) {
		return Result.from().setData("userId", userId).setData("gender", gender);
	}

	@Override
	public Result getUserInfo(Long userId, Integer age) {
		return Result.from().setData("userId", userId).setData("age", age);
	}

	@Override
	public Result getUserUserInfoAll(Long userId, Integer age, Long gender, User user) {
		return Result.from().setData("userId", userId).setData("age", age).setData("gender", gender).setData("user",
				user);
	}

	@Override
	public Result helloWorld() {
		return Result.from().setData("say","helloWorld");
	}

	@Override
	public Result getUserInfoFromQueryParamsParamFormatMap(User user) {
		return Result.from().setData("user", user);
	}

	@Override
	public Result getUserInfoFromQueryParamsParamFormatJSON(User user) {
		return Result.from().setData("user", user);
	}

	@Override
	public Result registerUserFromCookieMap(User user) {
		return Result.from().setData("user", user);
	}

	@Override
	public Result registerUserFromPathMap(User user) {
		return Result.from().setData("user", user);
	}

	@Override
	public Result registerUserFromHeaderMap(User user) {
		return Result.from().setData("user", user);
	}

}
