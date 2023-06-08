package com.systa.meetings.exceptions;

public class MeetingsFileNotValidException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String message;
    private Throwable ex;

    public MeetingsFileNotValidException( String message, Throwable ex) {
        super(message, ex);
        this.message = message;
        this.ex = ex;
    }

    public MeetingsFileNotValidException(String message) {
        super(message);
        this.message = message;
    }

}
