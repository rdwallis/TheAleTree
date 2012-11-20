package com.wallissoftware.ale.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.wallissoftware.ale.exceptions.InvalidHeirachyException;

@Cache(expirationSeconds=600)
@Entity
@Data
public class Team {
	
	private static int MAX_ACTIONS = 50;
	
	@Id
	private Long id;
	private int userCount;
	private long lastRefreshed;
	private List<Action> actions;
	
	public Team() {
		this.lastRefreshed = System.currentTimeMillis();
	}

	private Action getAction(long nodeId, ActionType actionType, Long parentId) throws InvalidHeirachyException {
		if (parentId != null) {
			if (nodeId == parentId) {
				throw new InvalidHeirachyException();
			}
		}
		Action search = new Action();
		search.setNodeId(nodeId);
		search.setActionType(actionType);
		search.setParentNodeId(parentId);
		if (getActions().contains(search)) {
			for (Action action: getActions()) {
				if (action.equals(search)) {
					return action;
				}
			}
		} else {
			if (getActions().size() > MAX_ACTIONS) {
				getActions().remove(0);
			}
			getActions().add(search);
		}
		return search;
	}
	
	public List<Action> getActions() {
		if (actions == null) {
			actions = new ArrayList<Action>();
		}
		return actions;
	}
	
	public boolean doAction(final Node node, final User user,
			final ActionType actionType, final Vote vote) {
		try {
			return doAction(node, user, actionType, vote, null);
		} catch (InvalidHeirachyException e) {
			//This won't happen because no parent is given.
			throw new RuntimeException();
		}
	}

	public boolean doAction(final Node node, final User user,
			final ActionType actionType, final Vote vote, final Node parent) throws InvalidHeirachyException {
		user.setLastActionTime(System.currentTimeMillis());
		Long parentId = null;
		if (parent != null) {
			parentId = parent.getId();
		}
		final Action action = getAction(node.getId(), actionType, parentId);
		final Vote originalStatus = action.getActionStatus();
		switch (vote) {
		case DOWN:
			if (user.getDownActions().add(action)) {
				if (user.getUpActions().remove(action)) {
					action.setUpVotes(action.getUpVotes() - 1);	
				}				
				action.setDownVotes(action.getDownVotes() + 1);
				if (actionType == ActionType.VOTE) {
					node.setDown(node.getDown() + 1);
				}
			}
			break;
		case NONE:
			if (user.getUpActions().remove(action)) {
				action.setUpVotes(action.getUpVotes() - 1);
				if (actionType == ActionType.VOTE) {
					node.setUp(node.getUp() - 1);
				}
			}
			if (user.getDownActions().remove(action)) {
				action.setDownVotes(action.getDownVotes() - 1);
				if (actionType == ActionType.VOTE) {
					node.setDown(node.getDown() - 1);
				}
			}
			break;
		case UP:
			if (user.getUpActions().add(action)) {
				if (user.getDownActions().remove(action)) {
					action.setDownVotes(action.getDownVotes() - 1);
				}
				action.setUpVotes(action.getUpVotes() + 1);
				if (actionType == ActionType.VOTE) {
					node.setUp(node.getUp() + 1);
				}
			}

			break;
		
		}
		
		final Vote status =action.getActionStatus();
		if (status != originalStatus) {
			node.revokeAction(originalStatus, actionType);
			node.performAction(status, actionType, parent);
			return true;
		} else {
			return false;
		}
	}

}
