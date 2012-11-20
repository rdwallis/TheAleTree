package com.wallissoftware.ale.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;
import lombok.extern.java.Log;

import com.wallissoftware.ale.model.Team;
import com.wallissoftware.ale.model.User;


@Log
public class UserDao {
	
	private static long TIME_OUT = 900000; //15 Minutes
	private static int MAX_TEAM_SIZE = 25;

	public User get(final String ipAddress) {
		log.info("IPAddress: " + ipAddress);
		User user = ofy().load().type(User.class).id(ipAddress).get();
		if (user == null) {
			user = new User(ipAddress);
		}
		if (user.getLastActionTime() < System.currentTimeMillis() - TIME_OUT) {
			assignTeam(user);
		}
		return user;
	}

	public Team assignTeam(User user) {
		Team team;
		team = ofy().load().type(Team.class).filter("userCount < ", MAX_TEAM_SIZE).order("-userCount").first().get();
		if (team == null) {
			team = new Team();
		} 
		team.setUserCount(team.getUserCount() + 1);
		if (team.getId() == null) {
			ofy().save().entities(team).now();
		} else {
			ofy().save().entities(team);
		}
		user.setTeam(team.getId());
		ofy().save().entities(user);
		return team;
	}
}
