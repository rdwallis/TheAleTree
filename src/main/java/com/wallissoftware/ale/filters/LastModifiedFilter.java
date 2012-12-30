package com.wallissoftware.ale.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.httpclient.util.DateUtil;

public class LastModifiedFilter implements Filter {

	private static Date lastModified = new Date();

	@Override
	public void init(FilterConfig fc) {
		//
	}

	@Override
	public void destroy() {
		//
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		if ((req instanceof HttpServletRequest)
				&& (res instanceof HttpServletResponse)) {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			

			try {
				Date since = DateUtil.parseDate(request
						.getHeader("If-Modified-Since"));
				if (!since.before(lastModified)) {
					response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				}
			} catch (Exception e) {

			}
			response.setHeader("Last-Modified", DateUtil.formatDate(lastModified));
			HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(
					(HttpServletResponse) response) {
				public void setHeader(String name, String value) {
					if (!"Last-Modified".equalsIgnoreCase(name)) {
						super.setHeader(name, value);
					}
				}
			};
			chain.doFilter(req, wrapper);
		} else {
			chain.doFilter(req, res);
		}
	}

	public static void setLastModified(Date date) {
		lastModified = date;

	}
}
