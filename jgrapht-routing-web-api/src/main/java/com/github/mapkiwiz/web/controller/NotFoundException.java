package com.github.mapkiwiz.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class NotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6363100115572059879L;

	public NotFoundException(String message) {
		super(message);
	}

}
