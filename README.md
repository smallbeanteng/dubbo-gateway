# dubbo-gateway #
dubbo-gateway 高性能dubbo网关，提供了http协议到dubbo协议的转换,但【并非】使用dubbo的【泛化】调用（泛化调用性能比普通调用有10-20%的损耗,通过普通异步的调用方式与基于webflux系列的响应式网关(spring cloud gateway)整合提高系统的吞吐量,普通调用需要依赖api jar包,需要对接口定义进行改造,除此之外不需要做任何其它改造.另外也支持基于servlet类的应用或网关(spring cloud zuul)进行整合
## 泛化缺点 ##
- 泛化过程数据流会经过了三次转换, 会产生大量的临时对象, 有很大的内存要求。使用反射方式对于旨在榨干服务器性能以获取高吞吐量的系统来说, 难以达到性能最佳
- 同时服务端也会对泛化请求多一重 Map <-> POJO 的来回转换的过程。整体上，与普通的Dubbo调用相比有10-20%的损耗
- 泛化调用在网关或服务消费者阶段无法校验参数类型及参数的有效性，数据要到服务提供者使用阶段时才能校验出参数的有效性
## 开源地址 ##
https://github.com/smallbeanteng/dubbo-gateway
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
			以上相当于会调用版本号为1.1.0并且groupw为userSystem的dubbo服务,与@DubboReference的参数对齐，具体支持哪些参数详见配置类DubboReferenceConfigProperties
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


参数注意事项：

@PathMapping参数 requestMethod：用于限定访问服务的方法，支持POST与GET,默认为POST

@FromCookie、@FromPath、@FromHeader、@FromQueryParams参数 paramFormat：

- JSON方式 限定参数名称对应的值为json字符串，然后通过反序列化json字符串得到参数对象(如@FromHeader("user") httpHeader应该要有一个头名称为user并且值为【样例数据】的json字符串(UrlEncode))，此将入参看做一个整体json字符串
- MAP方式 限定对象的propertyName与单个参数一一对应【例如@FromHeader(value="user",paramFormat =ParamFormat.MAP) httpHeader应该要有头名称为userName、password、age、gender、dt等与User对象属性对应的头信息，User将获取这些头信息最终组装成完整对象】,此方式不支持复杂嵌套对象,复杂嵌套对象请使用json,1.1.3+版本后默认为Map方式
- 此外如果方法的参数类型本身为基本数据类型，将固定使用Map方式，具体差异可以通过导入postman的测试用例体验

## 样例数据 ##
    {
    "userName": "admin",
    "password": "123456",
	"age": 99,
	"gender": 1,
    "dt": 1644431796892,
	"workHistory": {"workDescriptions": ["中学","大学"]} 
    }
## 使用步骤 ##
第一步：按照示例改造api接口，接口需要引入dubbo-gateway-api jar包
	
      	<dependency>
			<groupId>com.atommiddleware</groupId>
			<artifactId>dubbo-gateway-api</artifactId>
			<version>1.1.4.6-GA</version>
		</dependency>
第二步：网关引入改造后的jar包，同时引用以下jar包

	`	<dependency>
			<groupId>com.atommiddleware</groupId>
			<artifactId>dubbo-gateway-spring-boot-starter</artifactId>
			<version>1.1.4.6-GA</version>
		</dependency>`
第三步：在启动类上添加要扫描的api包名@DubboGatewayScanner(basePackages = "需扫描的api包名")

第四步: 网关配置routes（->看【配置中心】说明），如果只是单独的spring mvc则不需要配置了

第五步：没了...就是这么简单
## 项目说明 ##
- dubbo-gateway-api 是核心的api，相关注解都是在此项目中定义
- dubbo-gateway-core 核心实现，实现dubbo-gateway的相关逻辑都在此项目中
- dubbo-gateway-spring-boot-autoconfigure dubbo-gateway的自动装配
- dubbo-gateway-spring-boot-starter dubbo-gateway的starter
- dubbo-gateway-sample-api 示例服务api定义在此项目中
- dubbo-gateway-sample-provider 基于spring cloud的dubbo 服务提供者示例
- dubbo-gateway-sample 基于webflux(spring cloud gateway)的接入dubbo-gateway示例
- dubbo-gateway-sample-web-provider 基于sevlet类型的dubbo服务提供者示例
- dubbo-gateway-sample-web-consumer 基于sevlet类型spring mvc的项目接入dubbo-gateway示例
- dubbo-gateway-sample-zuul 基于spring cloud zuul 接入dubbo-gateway示例
- dubboGateWay.postman_collection.json 导出的一份postman自测用例
- dubboGateWay_XSS.postman_collection.json 导出的一份包含xss攻击代码的自测用例
## 配置中心 ##
按照dubbo的正常接入配置进行配置就好了，以下贴出例子使用的配置在nacos配置中心的配置,其中filters使用了Dubbo作为过滤器

服务提供者配置:

    dubbo:
      protocol:
        name: dubbo
        port: 20861
    server:
      port: 8861
    spring:
      cloud:
        nacos:
          discovery:
            namespace: dev
            server-addr: 127.0.0.1:8848
整合spring cloud gateway 网关配置：
    
    dubbo:
      cloud:
        subscribed-services: dubbo-gateway-sample-provider
    server:
      port: 8862
    spring:
      cloud:
        nacos:
          discovery:
            namespace: dev
            server-addr: 127.0.0.1:8848
        gateway:
          routes:
          - id: myGateway
            uri: dubbo://127.0.0.1:8862
            predicates:
            - Path=/**

整合spring cloud zuul网关配置:


    dubbo:
      cloud:
        subscribed-services: dubbo-gateway-sample-provider
    server:
      port: 8862
    spring:
      cloud:
        nacos:
          discovery:
            namespace: dev
            server-addr: 127.0.0.1:8848
      main:
        allow-bean-definition-overriding: true
    zuul:
	  ignored-patterns: /favicon.ico,/cas/redirect
      routes:
        dubboService:
          stripPrefix: false
          url: dubbo://127.0.0.1
          path: /**
注意：配置中的"D(d)ubbo"子眼表示使用的是dubbo gateway相关功能去处理路由，如果不配置则不会生效.
其中配置中uri(l) 配置除了dubbo相关字眼，其它信息并无实际意义，只是为了符合网关的配置规范要求，可以配成127.0.0.1等

整合spring mvc 配置：

    com.atommiddleware.cloud.config.includUrlPatterns=/sample/*,/order/*
    com.atommiddleware.cloud.config.excludUrlPatterns=

includUrlPatterns参数用于配置需要进行协议转换的url，excludUrlPatterns用于排除个别url,这两个参数只对【非】网关整合有效(因与网关整合path匹配交给了网关的path参数进行匹配)
## 安全 ##
xss防御 1.1.1版本+

参数校验组件 1.1.2版本+

cas 认证登录 1.1.3-beta+ spring mvc 与zuul 集成了spring security cas认证与spring session

配置:
	
    com:
      atommiddleware:
        cloud:
          config:
            security:
              cas:
                 enable: true
                 baseUrl: https://cas.atommiddleware.com:8812/sys/menusAndPermissions
                 serverUrl: https://cas.atommiddleware.com:8443/cas
                 principalAttrs: userId,companyCode,isSuperAdmin,username
                 ignoringUrls:
                 permitUrls:
                 anonymousUrls:
              xss:
                 enable: true
                 filterStrategy: 0
                 filterMode: 0
                 antisamyFileLocationPattern:
              csrf:
                 enable: false
	             domain: atommiddleware.com
	             path: /
	          cors:
	             enable: true
	             allowedOrigins: https://admin.atommiddleware.com:9628
	             allowedHeaders: X-Requested-With,content-type,X-XSRF-TOKEN
	             allowedMethods: GET,POST
	             maxAge: 3600 
            session:
              cookie:
                 enable: true
                 domain: atommiddleware.com
                 name: atomSessionId
                 path: /

## cas认证 ##
	
	cas认证中心开源传送门:https://github.com/apereo/cas
开启步骤:

	引入jar包:spring-boot-starter-security,spring-security-cas
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-cas</artifactId>
		</dependency>
	实现自己的用户定义，用户转换，权限逻辑主要有以下几个类需要实现
用户定义：org.springframework.security.core.userdetails 用户信息需要哪些属性
用户转换:com.atommiddleware.cloud.security.cas.CustomUserDetailsService 此类是默认实现，需要自己实现从cas认证中心登录成功后获取到的用户信息，转换为上一步的用户定义，如果是基于Url的权限定义可以使用com.atommiddleware.cloud.security.cas.PathPatternGrantedAuthority作为权限的载体，该类实现了match方法可以对url是否符合权限进行判断
权限逻辑:com.atommiddleware.cloud.security.cas.BasedVoter 实现对权限控制的逻辑，上一步用户转换可以给用户定义此登录用户的权限信息,在这一步可以获取到用户的权限信息，并加上验证逻辑

写好以上逻辑后上述类配置在Configuration中并且自定义实现在@AutoConfigureBefore(CasSecurityAutoConfiguration.class) 加载即可,再到spring.factories配置好此配置类

例子:

	@Configuration(proxyBeanMethods = false)
    @AutoConfigureBefore(CasSecurityAutoConfiguration.class)
    public class MyAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public BasedVoter basedVoter() {
		return new 自定义的BasedVoter;
	}

	@Bean
	@ConditionalOnMissingBean
	public CustomUserDetailsService customUserDetailsService() {
		return new 自定义的CustomUserDetailsServce;
	}
    }

在spring.factories中添加:

	org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    <包名>.MyAutoConfiguration	

 
参数配置说明:
![图片丢失了...](http://www.atommiddleware.com/cas.png)

## xss防御 ##
  参数配置说明:
![图片丢失了...](http://www.atommiddleware.com/xss.png)

## 参数校验 ##
  交给hibernate-validator框架注解配置

## csrf防御 ##
  参数配置说明
![图片丢失了...](http://www.atommiddleware.com/csrf.png)

## cors ##
  参数配置说明
![图片丢失了...](http://www.atommiddleware.com/cors.png)

## session共享 ##

开启步骤:

	引入jar包:spring-boot-starter-data-redis,spring-session-data-redis,commons-pool2
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.session</groupId>
			<artifactId>spring-session-data-redis</artifactId>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-pool2</artifactId>
		</dependency>

  参数配置说明:
![图片丢失了...](http://www.atommiddleware.com/session.png)

参数校验示例:
    
    public class Order implements Serializable {

	private static final long serialVersionUID = 1L;
	@NotBlank(message = "订单号不能为空")
	private String orderCode;

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

    }

## 序列化 ##
接口：com.atommiddleware.cloud.core.serialize
json序列化默认采用的是jackson,如果需要定制可以自行定制实现
## 输出响应 ##
spring cloud gateway类型接口:com.atommiddleware.cloud.core.annotation.ResponseReactiveResult

spring mvc 类型接口:com.atommiddleware.cloud.core.annotation.ResponseServletResult

spring cloud zuul类型接口:com.atommiddleware.cloud.core.annotation.ResponseZuulServletResult

默认实现添加了一些简单的头信息，如果需要定制实现可以自行实现接口

## 错误码表 ##
- 404 匹配的地址在dubbo未发现对应的服务
- 415 不支持的Media Type,默认支持[application/json,application/x-www-form-urlencoded]
- 500 内部服务器错误，一般为调用的dubbo服务抛出了异常或其它
- 405 方法不允许,默认只支持[post,get],并且要与@PathMapping的requestMethod参数匹配
- 400 错误的请求，一般情况是参数校验未通过
- 其它对照HttpStatus 

## 其它说明 ##
基于webflux的网关与基于servlet类的web应用接入整合方式是一样的步骤，例子使用的nacos版本2.0.3，默认支持GET,POST方式接入，ContentType支持application/json，application/x-www-form-urlencoded，复杂参数【类的属性为非基本数据类型】建议使用application/json,或项目整体都使用application/json
## 版本说明 ##
推荐试用1.1.4.6-GA
## feature ##
- spring cloud gateway 的安全实现【最好对接cas server】
- api jar包的热部署实现
- 实现根据元数据中心dubbo泛化调用

## 招募 ##
欢迎对微服务相关技术有研究并且热爱技术的兄弟加入此项目的开源队伍.

![图片不见了...](http://www.atommiddleware.com/author12.jpg)

## 打赏 ##
您的支持是一种动力.哈哈

![图片消失了...](http://www.atommiddleware.com/authorpay1.jpg)
    