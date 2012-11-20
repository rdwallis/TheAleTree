package com.wallissoftware.ale.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache(expirationSeconds=600)
@Entity
@Data
public class User {
	@Id private String ipAddress;
	private long created;
	private long lastActionTime;
	private Long team;
	private Set<Action> upActions;
	private Set<Action> downActions;
	
	@SuppressWarnings("unused")
	private User() {
		
	}
	
	public User(final String ipAddress) {
		this.ipAddress = ipAddress;
		this.created = System.currentTimeMillis();
	}
	
	public Set<Action> getUpActions() {
		if (upActions == null) {
			this.upActions = new HashSet<Action>();
		}
		return upActions;
	}
	
	public Set<Action> getDownActions() {
		if (downActions == null) {
			this.downActions = new HashSet<Action>();
		}
		return downActions;
	}
	
	
	
}
