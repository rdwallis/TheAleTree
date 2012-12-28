package com.wallissoftware.ale.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wallissoftware.ale.dao.NodeDao;
import com.wallissoftware.ale.dao.RssDao;
import com.wallissoftware.ale.dao.TeamDao;
import com.wallissoftware.ale.dao.UserDao;
import com.wallissoftware.ale.exceptions.InvalidHeirachyException;
import com.wallissoftware.ale.model.Team;
import com.wallissoftware.ale.model.User;

@Singleton
@Path("/admin")
public class AdminApi {

	private final NodeDao nodeDao;
	private final UserDao userDao;
	private final TeamDao teamDao;
	private final RssDao rssDao;

	@Inject
	private AdminApi(final NodeDao nodeDao, final UserDao userDao, final TeamDao teamDao, final RssDao rssDao) {
		this.nodeDao = nodeDao;
		this.userDao = userDao;
		this.teamDao = teamDao;
		this.rssDao = rssDao;
	}
	
	@GET
	@Path("/updatenoderanks")
	public void updateNodeRanks() {
		nodeDao.updateNodeRanks();
	}
	
	@POST
	@Path("/normalizenode/{id}/{ipAddress}")
	public void normalizeNode(@PathParam("id") final long id, @PathParam("ipAddress") final String ipAddress) throws InvalidHeirachyException {
		final User user = userDao.get(ipAddress);
		Team team = teamDao.get(user.getTeam());
		if (team == null) {
			team = userDao.assignTeam(user);
		}
		nodeDao.normalizeNode(nodeDao.get(id), user, team);
	}
	
	@GET
	@Path("/updaterss")
	public void updateRss() {
		rssDao.updateAll();
	}
	
}
