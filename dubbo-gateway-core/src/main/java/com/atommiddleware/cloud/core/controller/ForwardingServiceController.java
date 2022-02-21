package com.atommiddleware.cloud.core.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebInputException;

@RestController
public class ForwardingServiceController {

	@RequestMapping(value = "/cas/redirect", method = { RequestMethod.POST, RequestMethod.GET })
	public void redirect(@RequestParam("service") String service, HttpServletResponse response) {
		try {
			response.sendRedirect(service);
		} catch (IOException e) {
			throw new ServerWebInputException("service:" + service + " redirect fail");
		}
	}
}
