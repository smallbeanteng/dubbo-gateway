package com.atommiddleware.cloud.core.config;

public class ReferenceMethodConfig {

	private String name;

	private int timeout = -1;

	private int retries = -1;

	private String loadbalance = "";

	private boolean sent = true;

	private int actives = 0;

	private int executes = 0;

	private boolean deprecated = false;

	private boolean sticky = false;

	boolean isReturn = true;

	private String oninvoke = "";

	private String onreturn = "";

	private String onthrow = "";

	private String cache = "";

	private String validation = "";

	private String merger = "";

	private ReferenceArgument[] arguments = {};

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
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

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public int getActives() {
		return actives;
	}

	public void setActives(int actives) {
		this.actives = actives;
	}

	public int getExecutes() {
		return executes;
	}

	public void setExecutes(int executes) {
		this.executes = executes;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public boolean isSticky() {
		return sticky;
	}

	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

	public boolean isReturn() {
		return isReturn;
	}

	public void setReturn(boolean isReturn) {
		this.isReturn = isReturn;
	}

	public String getOninvoke() {
		return oninvoke;
	}

	public void setOninvoke(String oninvoke) {
		this.oninvoke = oninvoke;
	}

	public String getOnreturn() {
		return onreturn;
	}

	public void setOnreturn(String onreturn) {
		this.onreturn = onreturn;
	}

	public String getOnthrow() {
		return onthrow;
	}

	public void setOnthrow(String onthrow) {
		this.onthrow = onthrow;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public String getMerger() {
		return merger;
	}

	public void setMerger(String merger) {
		this.merger = merger;
	}

	public ReferenceArgument[] getArguments() {
		return arguments;
	}

	public void setArguments(ReferenceArgument[] arguments) {
		this.arguments = arguments;
	}

	public class ReferenceArgument {
		// argument: index -1 represents not set
		private int index = -1;

		// argument type
		private String type = "";

		// callback interface
		private boolean callback = false;

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public boolean isCallback() {
			return callback;
		}

		public void setCallback(boolean callback) {
			this.callback = callback;
		}

	}
}
