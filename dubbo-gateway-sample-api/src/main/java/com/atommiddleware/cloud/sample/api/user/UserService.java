package com.atommiddleware.cloud.sample.api.user;

import com.atommiddleware.cloud.api.annotation.FromBody;
import com.atommiddleware.cloud.api.annotation.FromCookie;
import com.atommiddleware.cloud.api.annotation.FromHeader;
import com.atommiddleware.cloud.api.annotation.FromPath;
import com.atommiddleware.cloud.api.annotation.FromQueryParams;
import com.atommiddleware.cloud.api.annotation.GateWayDubbo;
import com.atommiddleware.cloud.api.annotation.ParamAttribute.ParamFormat;
import com.atommiddleware.cloud.api.annotation.PathMapping;
import com.atommiddleware.cloud.api.annotation.PathMapping.RequestMethod;
import com.atommiddleware.cloud.sample.api.Result;
import com.atommiddleware.cloud.sample.api.user.domain.User;

@GateWayDubbo("userService")
public interface UserService {

	/**
	 * hello world
	 * @return hello
	 */
	@PathMapping(value="/sample/helloWorld",requestMethod=RequestMethod.GET)
	Result helloWorld();
	/**
	 * 参数为空post请求
	 * @return 结果
	 */
	@PathMapping(value="/sample/helloWorldPost",requestMethod=RequestMethod.POST)
	Result helloWorldPost();
	/**
	 * 返回值为空
	 */
	@PathMapping(value="/sample/helloVoid",requestMethod=RequestMethod.GET)
	void helloVoid();
	/**
	 * 返回值为空 post请求
	 */
	@PathMapping(value="/sample/helloVoidPost",requestMethod=RequestMethod.POST)
	void helloVoidPost();
	/**
	 * 注册用户
	 * @param user 用户信息
	 * @return 注册结果
	 */
	@PathMapping("/sample/registerUser")
	Result registerUser(@FromBody User user);
	/**
	 * 对象数据源来自header,headerName=user,headerValue=json(UrlEncoder后的字符串)
	 * @param user 用户信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/registerUserFromHeader",requestMethod=RequestMethod.GET)
	Result registerUserFromHeader(@FromHeader(value = "user",paramFormat = ParamFormat.JSON) User user);
	/**
	 * header中以key value方式传递对象参数,headerName=headerValue转换为beanPropertyName=beanPropertyValue
	 * headerName 对应bean 的propertyName,headerValue对应bean的propertyValue
	 * @param user 用戶信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/registerUserFromHeaderMap",requestMethod=RequestMethod.GET)
	Result registerUserFromHeaderMap(@FromHeader(value="user",paramFormat =ParamFormat.MAP) User user);
	/**
	 * 对象数据源来自cookie,cookieName=user,cookieValue=json(UrlEncoder后的字符串)
	 * @param user 用户信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/registerUserFromCookie",requestMethod=RequestMethod.GET)
	Result registerUserFromCookie(@FromCookie(value="user",paramFormat = ParamFormat.JSON) User user);
	/**
	 * cookie中以 key value 方式传递对象参数,cookieName=cookieValue转化为beanPropertyName=beanPropertyValue
	 * cookieName 对应bean 的propertyName,cookieValue对应bean的propertyValue,不支持嵌套对象转换，嵌套对象或复杂参数请用json
	 * @param user 用戶信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/registerUserFromCookieMap",requestMethod=RequestMethod.GET)
	Result registerUserFromCookieMap(@FromCookie(value="user",paramFormat = ParamFormat.MAP) User user);
	/**
	 * 对象数据源来自path,{user}=json(UrlEncoder后的字符串)
	 * @param user 用户信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/registerUserFromPath/{user}",requestMethod=RequestMethod.GET)
	Result registerUserFromPath(@FromPath(value="user",paramFormat = ParamFormat.JSON) User user);
	/**
	 * path pattern对应bean的属性名称
	 * @param user 用户信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/registerUserFromPathMap/{userName}/{age}/{gender}",requestMethod=RequestMethod.GET)
	Result registerUserFromPathMap(@FromPath(value="user",paramFormat = ParamFormat.MAP) User user);

	/**
	 * 对象参数来源于query json字符串,user=json(UrlEncoder后的字符串)
	 * @param user 用户信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/getUserInfoFromQueryParamsParamFormatJSON",requestMethod=RequestMethod.GET)
	Result getUserInfoFromQueryParamsParamFormatJSON(@FromQueryParams(value="user",paramFormat = ParamFormat.JSON)User user);
	
	/**
	 * 对象参数来源于query,以key,value方式传参,key对应bean propertyName,value对应propertyValue,嵌套对象或复杂对象请使用JSON
	 * @param user 用户
	 * @return 结果
	 */
	@PathMapping(value="/sample/getUserInfoFromQueryParamsParamFormatMap",requestMethod=RequestMethod.GET)
	Result getUserInfoFromQueryParamsParamFormatMap(@FromQueryParams(value="user",paramFormat = ParamFormat.MAP)User user);
	/**
	 * 数据来源queryParam
	 * @param userId 用户id
	 * @return 取消注销结果
	 */
	@PathMapping(value="/sample/unRegisterUser",requestMethod=RequestMethod.GET)
	Result unRegisterUser(@FromQueryParams("userId")Long userId);
	/**
	 * 数据来源path
	 * @param userId 用戶id
	 * @return 结果
	 */
	@PathMapping(value="/sample/getUserInfo/{userId}/{gender}",requestMethod=RequestMethod.GET)
	Result getUserInfo(@FromPath("userId") Long userId,@FromPath("gender") Short gender);
	/**
	 * 数据来源header 和cookie
	 * @param userId 用户id
	 * @param age 年龄
	 * @return 返回查询结果
	 */
	@PathMapping(value="/sample/getUserInfo/byHeaderAndCookie",requestMethod=RequestMethod.GET)
	Result getUserInfo(@FromHeader("userId")Long userId,@FromCookie("age") Integer age);

	/**
	 * 全场景
	 * @param userId 用户id
	 * @param age 年龄
	 * @param gender 性别
	 * @param user 用户信息
	 * @return 查询结果
	 */
	@PathMapping("/sample/getUserUserInfoAll/{userId}")
	Result getUserUserInfoAll(@FromPath("userId") Long userId,@FromCookie("age")Integer age,@FromHeader("gender")Long gender,@FromBody User user);
}
