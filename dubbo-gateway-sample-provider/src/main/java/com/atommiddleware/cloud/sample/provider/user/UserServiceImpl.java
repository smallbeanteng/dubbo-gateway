package com.atommiddleware.cloud.sample.provider.user;

import org.apache.dubbo.config.annotation.DubboService;

import com.atommiddleware.cloud.sample.api.Result;
import com.atommiddleware.cloud.sample.api.user.UserService;
import com.atommiddleware.cloud.sample.api.user.domain.User;

@DubboService
public class UserServiceImpl implements UserService {
	@Override
	public Result registerUser(User user) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return Result.from().setData("userId", userId).setData("age", age).setData("gender", gender).setData("user", user);
	}

}
