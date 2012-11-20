package com.wallissoftware.ale.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.googlecode.objectify.annotation.Embed;

@Embed
@Data
@EqualsAndHashCode(exclude={"upVotes", "downVotes"})
public class Action {
	public final static int THRESHOLD = 5;
	private long nodeId;
	private Long parentNodeId;
	private ActionType actionType;
	private int upVotes;
	private int downVotes;
	
	public Vote getActionStatus() {
		final int diff = getUpVotes()- getDownVotes(); 
		if (Math.abs(diff) < THRESHOLD) {
			return Vote.NONE;
		} else 	if (diff > 0) {
			return Vote.UP;
		} else {
			return Vote.DOWN;
		}
	}
	
	
}
