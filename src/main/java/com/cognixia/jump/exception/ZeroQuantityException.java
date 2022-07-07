package com.cognixia.jump.exception;

public class ZeroQuantityException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public ZeroQuantityException(String resource, String message) {
		super(resource + " with id(s) = " + message + "could not be checked out");
	}

}
