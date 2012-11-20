package com.wallissoftware.ale.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class JsonPFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		final String callback = request.getParameter("callback");
		if ((callback != null) && (!callback.isEmpty()) && (request.getContentType() != null) && (request.getContentType().equals("application/json"))) {
			 ServletOutputStream out = response.getOutputStream();
		     out.print(callback+ "(");
		     chain.doFilter(request, response);
		     out.print(");");
		} else {
			chain.doFilter(request, response);
		}
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
