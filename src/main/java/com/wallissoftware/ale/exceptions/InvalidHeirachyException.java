package com.wallissoftware.ale.exceptions;

public class InvalidHeirachyException extends Exception {

	private static final long serialVersionUID = 258466723828129778L;

	@Override
	public String getMessage() {
		return "A node may not have itself as a parent or child";
	}
	
	

	
	
}
