package com.wallissoftware.ale.exceptions;

public class InvalidNodeException extends Exception {

	private static final long serialVersionUID = -5031992609302522797L;

	@Override
	public String getMessage() {
		return "No url or comment provided";
	}
	
	

}
