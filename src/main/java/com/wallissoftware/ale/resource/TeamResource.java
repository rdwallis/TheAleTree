package com.wallissoftware.ale.resource;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wallissoftware.ale.dao.TeamDao;
import com.wallissoftware.ale.dao.UserDao;
import com.wallissoftware.ale.model.Action;
import com.wallissoftware.ale.model.Team;
import com.wallissoftware.ale.model.User;
import com.wallissoftware.ale.model.Vote;

@Singleton
@Path("/team")
public class TeamResource {

	private final UserDao userDao;
	private final TeamDao teamDao;

	@Inject
	private TeamResource(final UserDao userDao, final TeamDao teamDao) {
		this.userDao = userDao;
		this.teamDao = teamDao;
	}
	
	@GET
	@Path("/pending")
	@Produces({"application/json"})
	public List<Action> getActions(@QueryParam("offset") @DefaultValue("0") final int offset, @Context final HttpServletRequest req) {
		final User user = userDao.get(req.getLocalAddr());
		Team team = teamDao.get(user.getTeam());
		if (team == null) {
			team = userDao.assignTeam(user);
		}
		team.getActions().remove(user.getDownActions());
		team.getActions().remove(user.getUpActions());
		Iterator<Action> it = team.getActions().iterator();
		while (it.hasNext()) {
			if (it.next().getActionStatus() != Vote.NONE) {
				it.remove();
			}
		}
		return team.getActions();
	}
	
}
