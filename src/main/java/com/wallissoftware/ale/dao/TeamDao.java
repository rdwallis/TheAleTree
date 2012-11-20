package com.wallissoftware.ale.dao;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.wallissoftware.ale.model.Team;


public class TeamDao {

	public Team get(final long id) {
		return ofy().load().type(Team.class).id(id).get();
	}

}
