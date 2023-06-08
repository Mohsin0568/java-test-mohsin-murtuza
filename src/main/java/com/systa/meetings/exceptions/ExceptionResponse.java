package com.systa.meetings.exceptions;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionResponse {
	
	private LocalDateTime timeStamp;
	private String message;

}
