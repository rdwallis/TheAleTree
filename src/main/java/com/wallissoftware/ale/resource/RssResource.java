package com.wallissoftware.ale.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wallissoftware.ale.dao.RssDao;

@Singleton
@Path("/rss")
public class RssResource {

	private final RssDao rssDao;

	@Inject
	private RssResource(RssDao rssDao) {
		this.rssDao = rssDao;
	}
	
	@GET
	@Path("/create")
	public void createRss(@QueryParam("url") final String url, @Context final HttpServletRequest req) {
		rssDao.create(url, req.getRemoteAddr());
	}
	
	@GET
	@Path("/updaterss")
	public void updateRss() {
		rssDao.updateAll();
	}
	
}
