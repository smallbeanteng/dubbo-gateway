# dubbo-gateway #
dubbo-gateway 提供了http协议到dubbo协议的转换,但【并非】使用dubbo的【泛化】调用（泛化调用性能比普通调用有10-20%的损耗,通过普通异步的调用方式与基于webflux系列的响应式网关整合提高系统的吞吐量,普通调用需要依赖api jar包,需要对接口定义进行改造,除此之外不需要做任何其它改造.另外也支持基于servlet类的应用或网关进行整合
## 泛化缺点 ##
- 泛化过程数据流会经过了三次转换, 会产生大量的临时对象, 有很大的内存要求。使用反射方式对于旨在榨干服务器性能以获取高吞吐量的系统来说, 难以达到性能最佳
- 同时服务端也会对泛化请求多一重 Map <-> POJO 的来回转换的过程。整体上，与普通的Dubbo调用相比有10-20%的损耗
- 泛化调用在网关或服务消费者阶段无法校验参数类型的有效性，数据要到服务提供者反序列化时才能校验出参数类型的有效性
## 相关注解 ##
## @GateWayDubbo ##
标识这个接口需要自动进行协议转换

    /**
	 * 服务id,可以和dubbo普通调用的配置属性关联.
	 */
	@AliasFor("id")
	String value() default "";

	/**
	 * 服务id,可以和dubbo普通调用的配置属性关联.
	 * 例如: 
			com.atommiddleware.cloud.config.dubboRefer.<userService>.version=1.1.0
			com.atommiddleware.cloud.config.dubboRefer.<userService>.group=userSystem
			以上相当于会调用版本号为1.1.0并且groupw为userSystem的dubbo服务,与@DubboReference的参数对齐，具体支持哪些参数详见配置类
	 */
	@AliasFor("value")
	String id() default "";
## @PathMapping ##
标记这个接口方法需要进行协议自动转换
    
    /**
	 * 路径表达式
	 */
	@AliasFor("path")
	String value() default "";

	/**
	 * 路径表达式
	 */
	@AliasFor("value")
	String path() default "";
	
	/**
	 * 提交方法，GET或POST
	 */
	RequestMethod requestMethod() default RequestMethod.POST;
	
	public enum RequestMethod {
		GET, POST

	}
## @FromBody ##
表示参数对象来源于消息体

    /**
	 * 是否检查参数
	 */
	@AliasFor(annotation = ParamAttribute.class)
	boolean required() default true;
## @FromHeader ##
表示参数对象来源于消息头

    /**
	 * 消息头名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String value() default "";

	/**
	 * 消息头名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String name() default "";
	
	/**
	 * 是否检查参数
	 */
	@AliasFor(annotation = ParamAttribute.class)
	boolean required() default true;
## @FromCookie ##
表示参数对象来源于cookie

    /**
	 * cookie名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String value() default "";

	/**
	 * cookie名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String name() default "";

	/**
	 * 是否检查参数
	 */
	@AliasFor(annotation = ParamAttribute.class)
	boolean required() default true;
## @FromPath ##
表示参数对象来源于path,支持path表达式

    /**
	 * path占位符名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String value() default "";

	/**
	 * path占位符名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String name() default "";
	
	/**
	 * 是否检查参数
	 */
	@AliasFor(annotation = ParamAttribute.class)
	boolean required() default true;
## @FromQueryParams ##
表示参数来源于query部分

	/**
	 * query名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String value() default "";

	/**
	 * query名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String name() default "";
	
	/**
	 * 是否检查参数
	 */
	@AliasFor(annotation = ParamAttribute.class)
	boolean required() default true;
## @FromAttribute ##
表示参数来源于attribute

    /**
	 * attribute 名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String value() default "";

	/**
	 * attribute 名称
	 */
	@AliasFor(annotation = ParamAttribute.class)
	String name() default "";

	/**
	 * 是否检查参数
	 */
	@AliasFor(annotation = ParamAttribute.class)
	boolean required() default true;
## 配置示例 ##
    @GateWayDubbo("userService")
    public interface UserService {
    /**
	 * 数据来源消息体
	 */
	@PathMapping("/sample/registerUser")
	Result registerUser(@FromBody User user);
	/**
	 * 对象数据源来自header
	 * @param user 用户信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/registerUserFromHeader",requestMethod=RequestMethod.GET)
	Result registerUserFromHeader(@FromHeader("user") User user);
	/**
	 * 对象数据源来自cookie
	 * @param user 用户信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/registerUserFromCookie",requestMethod=RequestMethod.GET)
	Result registerUserFromCookie(@FromCookie("user") User user);
	/**
	 * 对象数据源来自path
	 * @param user 用户信息
	 * @return 结果
	 */
	@PathMapping(value="/sample/registerUserFromPath/{user}",requestMethod=RequestMethod.GET)
	Result registerUserFromPath(@FromPath("user") User user);
	/**
	 * 数据来源queryParam
	 * @param userId 用户id
	 * @return 取消注销结果
	 */
	@PathMapping(value="/sample/unRegisterUser",requestMethod=RequestMethod.GET)
	Result unRegisterUser(@FromQueryParams("userId")Long userId);
	/**
	 * 数据来源path
	 * @param userId
	 * @return
	 */
	@PathMapping(value="/sample/getUserInfo/{userId}/{gender}",requestMethod=RequestMethod.GET)
	Result getUserInfo(@FromPath("userId") Long userId,@FromPath("gender") Short gender);
	/**
	 * 数据来源header 和cookie
	 * @param userId 用户id
	 * @param age 年龄
	 * @return 返回插叙结果
	 */
	@PathMapping(value="/sample/getUserInfo/byHeaderAndCookie",requestMethod=RequestMethod.GET)
	Result getUserInfo(@FromHeader("userId")Long userId,@FromCookie("age")Integer age);
	
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
## 使用步骤 ##
第一步：按照示例改造api接口，接口需要引入dubbo-gateway-api jar包
	
      	<dependency>
			<groupId>com.atommiddleware</groupId>
			<artifactId>dubbo-gateway-api</artifactId>
			<version>1.0.0</version>
		</dependency>
第二步：网关引入改造后的jar包，同时引用以下jar包

	`	<dependency>
			<groupId>com.atommiddleware</groupId>
			<artifactId>dubbo-gateway-spring-boot-starter</artifactId>
			<version>1.0.0</version>
		</dependency>`
第三步：没有了。。就是这么简单
## 项目说明 ##
- dubbo-gateway-api 是核心的api，相关注解都是在此项目中定义
- dubbo-gateway-core 核心实现，实现dubbo-gateway的相关逻辑都在此项目中
- dubbo-gateway-spring-boot-autoconfigure dubbo-gateway的自动装配
- dubbo-gateway-spring-boot-starter dubbo-gateway的starter
- dubbo-gateway-sample-api 示例服务api定义在此项目中
- dubbo-gateway-sample-provider 基于spring cloud的dubbo 服务提供者示例
- dubbo-gateway-sample 基于webflux(spring cloud gateway、zuul2)的接入dubbo-gateway示例
- dubbo-gateway-sample-web-provider 基于sevlet类型的dubbo服务提供者示例
- dubbo-gateway-sample-web-consumer 基于sevlet类型的项目(包括网关比如zuul-1)接入dubbo-gateway示例
## 配置中心 ##
按照dubbo的正常接入配置进行配置就好了，以下贴出例子使用的配置在nacos配置中心的配置

服务提供者配置:
	
    server.port=8861
    dubbo.protocol.port=20861
    dubbo.protocol.name=dubbo
    spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
    spring.cloud.nacos.discovery.namespace=dev
服务消费者配置：
    
    server.port=8862
    dubbo.cloud.subscribed-services=dubbo-gateway-sample-provider
    spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
    spring.cloud.nacos.discovery.namespace=dev
## 其它说明 ##
基于webflux的网关与基于servlet类的web应用接入整合方式是一样的步骤，例子使用的nacos版本2.0.3

    