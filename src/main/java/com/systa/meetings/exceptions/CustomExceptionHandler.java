/**
 * 
 */
package com.systa.meetings.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author mohsin
 *
 */

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(MeetingsFileNotValidException.class)
	public final ResponseEntity<ExceptionResponse> handleServerException(MeetingsFileNotValidException ex){
		ExceptionResponse response = new ExceptionResponse(LocalDateTime.now(), ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ExceptionResponse> handleAllException(Exception ex) {
		ex.printStackTrace();
		ExceptionResponse response = new ExceptionResponse(LocalDateTime.now(), ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
