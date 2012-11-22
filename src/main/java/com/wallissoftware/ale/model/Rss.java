package com.wallissoftware.ale.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache(expirationSeconds=600)
@Entity
@Data
public class Rss {
	@Id private String url;
	private Set<String> users;
	@Index private long lastUpdated;
	
	public Set<String> getUsers() {
		if (users == null) {
			users = new HashSet<String>();
		}
		return users;
	}
}
