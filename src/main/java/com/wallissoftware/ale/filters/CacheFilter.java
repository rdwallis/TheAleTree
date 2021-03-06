package com.wallissoftware.ale.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CacheFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String maxAge = "300";
		if (response instanceof HttpServletResponse) {
			
			String path = ((HttpServletRequest) request).getRequestURI();
			if (path.startsWith("/images")) {
				maxAge = "31557600";
			} else if ((path.startsWith("/docs")) || path.endsWith("/attach") || path.endsWith(".ico")){
				maxAge= "86400";
			} else if (path.endsWith("up") || path.endsWith("down") || path.endsWith("spam") || path.endsWith("notspam") || path.endsWith("link") || path.endsWith("unlink")) {
				maxAge= "0";
			}
			
			if (path.equals("/") || path.startsWith("/1")) {
				((HttpServletResponse) response).setContentType("text/html");
				((HttpServletResponse) response).setCharacterEncoding("UTF-8");
			}
			
			((HttpServletResponse) response).setHeader("Vary", "Accept-Encoding");
			if (!path.startsWith("/1/admin")) {
				((HttpServletResponse) response).setHeader("Cache-Control", "public, max-age=" + maxAge);
				((HttpServletResponse) response).setHeader("Pragma", "Public");
				chain.doFilter(request, new HttpServletResponseWrapper((HttpServletResponse) response) {
			            public void setHeader(String name, String value) {
			                if (!"ETag".equalsIgnoreCase(name) && !"Pragma".equalsIgnoreCase(name) && !"Cache-Control".equalsIgnoreCase(name)) {
			                    super.setHeader(name, value);
			                }
			            }
			        });
			} else {
				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}
		

		
		
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
