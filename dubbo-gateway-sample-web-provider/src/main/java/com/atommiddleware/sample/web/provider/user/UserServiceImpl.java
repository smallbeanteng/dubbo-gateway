package com.atommiddleware.sample.web.provider.user;

import org.apache.dubbo.config.annotation.DubboService;

import com.atommiddleware.cloud.sample.api.Result;
import com.atommiddleware.cloud.sample.api.user.UserService;
import com.atommiddleware.cloud.sample.api.user.domain.User;

@DubboService
public class UserServiceImpl implements UserService {
	@Override
	public Result registerUser(User user) {
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

	@Override
	public Result helloWorld() {
		return Result.from().setData("say","helloWorld");
	}

	@Override
	public Result registerUserFromHeaderMap(User user) {
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
	public Result getUserInfoFromQueryParamsParamFormatJSON(User user) {
		return Result.from().setData("user", user);
	}

	@Override
	public Result getUserInfoFromQueryParamsParamFormatMap(User user) {
		return Result.from().setData("user", user);
	}

	@Override
	public void helloVoid() {
		System.out.println("void");
	}

	@Override
	public Result helloWorldPost() {
		return Result.from().setData("say","helloWorld post");
	}

	@Override
	public void helloVoidPost() {
		System.out.println("helloVoidPost");
	}

}
