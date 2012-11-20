package com.wallissoftware.ale.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import org.apache.commons.lang3.StringEscapeUtils;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.petebevin.markdown.MarkdownProcessor;
import com.wallissoftware.ale.exceptions.InvalidNodeException;

@Cache(expirationSeconds=600)
@Entity
@Data
public class Node {
	
	@Id private Long id;
	
	@Index private Set<Long> parents;
	
	private long childCount;
	
	@Index private String url;
	
	private String comment;
	
	private String title;
	
	private long created;
	
	@Index private long reset;
	
	private long teamUp;
	
	private long teamDown;
	
	private long up;
	
	private long down;
	
	private long spam;
	
	private long notSpam;
	
	private Long redirect;
	
	private double wilsonScore;
	
	@Index private double weightedWilsonScore;
	
	private double spamWilsonScore;
	
	private long lastCalculationTime;
	
	private int delayIncrement;
	
	private long nextCalculationTime;
	
	protected Node() {
		
	}

	public Node(final String url, final String comment, final long parentId) throws InvalidNodeException {
		this.setCreated(System.currentTimeMillis());
		this.setReset(this.getCreated());
		
		if (url != null) {
			this.setUrl(url); 
			this.setTitle(getUrl());
		} else if (comment != null){
			this.setComment(comment);
		} else {
			throw new InvalidNodeException();
		}
		
		this.getParents().add(parentId);
		updateWilsonScore();
	}
	
	public void setComment(final String comment) {
		this.comment = new MarkdownProcessor().markdown((StringEscapeUtils.escapeHtml4(comment)));
	}
	
	public void setUrl(String url) {
		url = url.trim();
		if (!url.contains("://")) {
			url = "http://" + url;
		}
		this.url = StringEscapeUtils.escapeHtml4(url);
	}

	public Set<Long> getParents() {
		if (parents == null) {
			parents = new HashSet<Long>();
		}
		return parents;
	}
	
	public void revokeAction(final Vote vote, final ActionType actionType) {
		doAction(vote, actionType, -1, null);	
	}
	
	public void performAction(final Vote vote, final ActionType actionType, final Node parent) {
		doAction(vote, actionType, 1, parent);		
	}
	
	private void doAction(final Vote vote, final ActionType actionType, final int increment, final Node parent) {
		switch (vote) {
		case DOWN:
			switch (actionType) {
			case LINK:
				if (parent != null) {
					if (increment > 0) {
						this.getParents().remove(parent.getId());
						this.decChildCount();
					} 
				}
				break;
			case SPAM:
				setSpam(getSpam() + increment);
				break;
			case VOTE:
				setTeamDown(getTeamDown() + increment);
				break;
			
			}
			
			break;
		case NONE:
			break;
		case UP:
			switch (actionType) {
			case LINK:
				if (parent != null) {
					if ((increment > 0) && (parent.getId() != this.getId())){
						this.getParents().add(parent.getId());
						this.incChildCount();
					}
				}
				break;
			case SPAM:
				setNotSpam(getNotSpam() + increment);
				break;
			case VOTE:
				setTeamUp(getTeamUp() + increment);
				break;
			
			}
		
		}
	}
	
	public void setTeamUp(final long teamUp) {
		this.teamUp = teamUp;
		setDelayIncrement(0);
		updateWilsonScore();
	}
	
	public void setTeamDown(final long teamDown) {
		this.teamDown = teamDown;
		setDelayIncrement(0);
		updateWilsonScore();
	}
	
	public void setSpam(final long spam) {
		this.spam = spam;
		setSpamWilsonScore(calcWilsonScore(getSpam(), getNotSpam()));
	}
	
	public void setNotSpam(final long notSpam) {
		this.notSpam = notSpam;
		setSpamWilsonScore(calcWilsonScore(getSpam(), getNotSpam()));
	}
	
	
	private void updateWilsonScore() {
		setWilsonScore(calcWilsonScore(getTeamUp(), getTeamDown()));
	    updateWeightedWilsonScore();
	    
	}
	
	public void setRedirect(final long redirect) {
		this.redirect = redirect;
		updateWeightedWilsonScore();
	}
	
	public void updateWeightedWilsonScore() {
		if (this.getRedirect() != null) {
			setWeightedWilsonScore(Double.MIN_VALUE);
			setNextCalculationTime(Long.MAX_VALUE);
			return;
		}
		
		long diff = System.currentTimeMillis() - getReset();
	    double weighting = Math.max(1, Math.log(diff / 3600000)); // 3600000 == 1 hour
	    setWeightedWilsonScore(getWilsonScore() / weighting);
	    
	    setLastCalculationTime(System.currentTimeMillis());

	    long delay = (long) (Math.pow(2, getDelayIncrement()) * 3600000); 
	    setDelayIncrement(getDelayIncrement() + 1);
	    setNextCalculationTime(System.currentTimeMillis() + delay);
	}

	private double calcWilsonScore(long up, long down) {
		long n = up + down;
	    if (n == 0) {
	        return 0;
	    }
	    double z = 1; //1.0 = 85%, 1.6 = 95%
	    double phat = up / n;
		return Math.sqrt(phat+z*z/(2*n)-z*((phat*(1-phat)+z*z/(4*n))/n))/(1+z*z/n);
	}
	
	public void decChildCount() {
		childCount--;
	}
	
	public void incChildCount() {
		childCount++;
	}
	
}
