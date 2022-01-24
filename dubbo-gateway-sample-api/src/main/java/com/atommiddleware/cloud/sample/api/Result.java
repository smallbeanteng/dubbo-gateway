package com.atommiddleware.cloud.sample.api;

import java.util.HashMap;

public class Result extends HashMap<String, Object> {

	public Result() {
		this.setCode(0);
	}

	public Result(int code) {
		this(code, null);
	}

	public Result(int code, String msg) {
		this.setCode(code);
		this.put("msg", msg);
	}

	public Result setData(String key, Object value) {
		this.put(key, value);
		return this;
	}

	public Integer getCode() {
		return (Integer) this.get("code");
	}

	public void setCode(Integer code) {
		this.put("code", code);
	}

	public String getMsg() {
		return (String) this.get("msg");
	}

	public void setMsg(String msg) {
		this.put("msg", msg);
	}

	public static Result from() {
		return new Result();
	}

	public static Result fromCode(int code) {
		return new Result(code);
	}

	public static Result fromCodeAndMsg(int code, String msg) {
		return new Result(code, msg);
	}
}
