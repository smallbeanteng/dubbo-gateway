package com.atommiddleware.cloud.core.config;

import org.apache.dubbo.common.constants.ClusterRules;
import org.apache.dubbo.common.constants.LoadbalanceRules;
import org.apache.dubbo.common.constants.RegistryConstants;

public class DubboReferenceConfig {

	/**
	 * Interface class name, default value is empty string
	 */
	private String interfaceName = "";

	/**
	 * Service version, default value is empty string
	 */
	private String version = "";

	/**
	 * Service group, default value is empty string
	 */
	private String group = "";

	/**
	 * Service target URL for direct invocation, if this is specified, then registry
	 * center takes no effect.
	 */
	private String url = "";

	/**
	 * Client transport type, default value is "netty"
	 */
	private String client = "";

	/**
	 * Check if service provider is available during boot up, default value is true
	 */
	private boolean check = true;

	/**
	 * Whether eager initialize the reference bean when all properties are set,
	 * default value is false
	 */
	private boolean init = false;

	/**
	 * Whether to make connection when the client is created, the default value is
	 * false
	 */
	private boolean lazy = false;

	/**
	 * Whether to reconnect if connection is lost, if not specify, reconnect is
	 * enabled by default, and the interval for retry connecting is 2000 ms
	 * <p>
	 * see org.apache.dubbo.remoting.Constants#DEFAULT_RECONNECT_PERIOD
	 */
	private String reconnect = "";

	/**
	 * Whether to stick to the same node in the cluster, the default value is false
	 * <p>
	 * see Constants#DEFAULT_CLUSTER_STICKY
	 */
	private boolean sticky = false;

	/**
	 * Cluster strategy, you can use
	 * {@link org.apache.dubbo.common.constants.ClusterRules#FAIL_FAST} ……
	 */
	private String cluster = ClusterRules.EMPTY;

	/**
	 * Maximum connections service provider can accept, default value is 0 -
	 * connection is shared
	 */
	private int connections = 0;

	/**
	 * Service owner, default value is empty string
	 */
	private String owner = "";

	/**
	 * Service layer, default value is empty string
	 */
	private String layer = "";

	/**
	 * Service invocation retry times
	 * <p>
	 * see Constants#DEFAULT_RETRIES
	 */
	private int retries = 2;

	/**
	 * Load balance strategy, you can use
	 * {@link org.apache.dubbo.common.constants.LoadbalanceRules#RANDOM} ……
	 */
	private String loadbalance = LoadbalanceRules.EMPTY;

	/**
	 * Maximum active requests allowed, default value is 0
	 */
	private int actives = 0;

	/**
	 * Service mock name, use interface name + Mock if not set
	 */
	private String mock = "";

	/**
	 * Whether to use JSR303 validation, legal values are: true, false
	 */
	private String validation = "";

	/**
	 * Timeout value for service invocation, default value is 0
	 */
	private int timeout = 0;

	/**
	 * Specify cache implementation for service invocation, legal values include:
	 * lru, threadlocal, jcache
	 */
	private String cache = "";

	/**
	 * Filters for service invocation
	 * <p>
	 * see Filter
	 */
	private String[] filter = {};

	/**
	 * Listeners for service exporting and unexporting
	 * <p>
	 * see ExporterListener
	 */
	private String[] listener = {};

	/**
	 * Customized parameter key-value pair, for example: {key1, value1, key2,
	 * value2}
	 */
	private String[] parameters = {};

	/**
	 * Application associated name
	 */
	private String application = "";

	/**
	 * Module associated name
	 */
	private String module = "";

	/**
	 * Consumer associated name
	 */
	private String consumer = "";

	/**
	 * Monitor associated name
	 */
	private String monitor = "";

	/**
	 * Registry associated name
	 */
	private String[] registry = {};

	/**
	 * Service tag name
	 */
	private String tag = "";

	/**
	 * Service merger
	 */
	private String merger = "";

	/**
	 * methods support
	 */
	private ReferenceMethodConfig[] methods = {};

	/**
	 * The id
	 *
	 * @return default value is empty
	 * @since 2.7.3
	 */
	private String id = "";

	/**
	 * declares which app or service this interface belongs to
	 * 
	 * @see RegistryConstants#PROVIDED_BY
	 */
	private String[] providedBy = {};

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public String getReconnect() {
		return reconnect;
	}

	public void setReconnect(String reconnect) {
		this.reconnect = reconnect;
	}

	public boolean isSticky() {
		return sticky;
	}

	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public int getConnections() {
		return connections;
	}

	public void setConnections(int connections) {
		this.connections = connections;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public String getLoadbalance() {
		return loadbalance;
	}

	public void setLoadbalance(String loadbalance) {
		this.loadbalance = loadbalance;
	}

	public int getActives() {
		return actives;
	}

	public void setActives(int actives) {
		this.actives = actives;
	}

	public String getMock() {
		return mock;
	}

	public void setMock(String mock) {
		this.mock = mock;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String[] getFilter() {
		return filter;
	}

	public void setFilter(String[] filter) {
		this.filter = filter;
	}

	public String[] getListener() {
		return listener;
	}

	public void setListener(String[] listener) {
		this.listener = listener;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public String getMonitor() {
		return monitor;
	}

	public void setMonitor(String monitor) {
		this.monitor = monitor;
	}

	public String[] getRegistry() {
		return registry;
	}

	public void setRegistry(String[] registry) {
		this.registry = registry;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getMerger() {
		return merger;
	}

	public void setMerger(String merger) {
		this.merger = merger;
	}

	public ReferenceMethodConfig[] getMethods() {
		return methods;
	}

	public void setMethods(ReferenceMethodConfig[] methods) {
		this.methods = methods;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getProvidedBy() {
		return providedBy;
	}

	public void setProvidedBy(String[] providedBy) {
		this.providedBy = providedBy;
	}

}
