package com.atommiddleware.cloud.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class HttpUtils {

	/**
	 * 将URL请求参数转换成Map
	 * 
	 * @param request
	 */
	public static Map<String, String> getUrlParams(HttpServletRequest request,String charset) {
		Map<String, String> result = new HashMap<>(16);
		String param = "";
		try {
			String urlPar = request.getQueryString();
			if (!StringUtils.isEmpty(urlPar)) {
				param = URLDecoder.decode(urlPar, charset);
			} else {
				return result;
			}
		} catch (UnsupportedEncodingException e) {
		}
		String[] params = param.split("&");
		for (String s : params) {
			int index = s.indexOf("=");
			result.put(s.substring(0, index), s.substring(index + 1));
		}
		return result;
	}
	
	/**
	 * 获取 Body 参数
	 * 
	 * @param request
	 */
	public static String getBodyParam(final HttpServletRequest request) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String str = "";
		StringBuilder wholeStr = new StringBuilder();
		// 一行一行的读取body体里面的内容；
		while ((str = reader.readLine()) != null) {
			wholeStr.append(str);
		}
		return wholeStr.toString();
	}
	
}
