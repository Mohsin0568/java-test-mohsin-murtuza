/**
 * 
 */
package com.systa.meetings.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.systa.meetings.domain.BookingRequest;
import com.systa.meetings.domain.MeetingRequest;
import com.systa.meetings.exceptions.MeetingsFileNotValidException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mohsin
 *
 */

@Service
@Slf4j
public class FileService {

	public MeetingRequest getMeetingRequestData(MultipartFile file){
		
		try {
			Set<BookingRequest> bookingRequests = new TreeSet<>();
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			
			String firstLine = br.readLine(); // reading first line of the file
	        if(firstLine == null) {
	        	// throwing exception as file is empty
	        	throw new MeetingsFileNotValidException("File is empty");
	        }
	        
	        String[] officeTimings = firstLine.split(" ");
	        LocalTime officeStartTime = getTimeFromString(officeTimings[0]);
	        LocalTime officeEndTime = getTimeFromString(officeTimings[1]);
	        
	        log.info("Office start time is {}", officeStartTime);
	        log.info("Office End time is {}", officeEndTime);
	        
	        String line = "";
	        while((line = br.readLine()) != null) { // reading first line of booking request
	        	String[] bookingRequestFirstLine = line.split(" ");
	        	LocalDateTime requestTime = getLocalDateTimeFromString(bookingRequestFirstLine[0] + " " + bookingRequestFirstLine[1], "yyyy-MM-dd HH:mm:ss");
	        	String empId = bookingRequestFirstLine[2];
	        	
	        	line = br.readLine(); // reading second line of booking request
	        	if(line == null) {         		
	        		// throwing exception as booking request data does not have second line
		        	throw new MeetingsFileNotValidException("Data is not correct");
	        	}
	
	        	String[] bookingRequestSecondLine = line.split(" ");	
	        	LocalDate meetingDate = LocalDate.parse(bookingRequestSecondLine[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")) ;
	        	LocalTime meetingTime = LocalTime.parse(bookingRequestSecondLine[1], DateTimeFormatter.ofPattern("HH:mm")) ;
	        	
	        	int duration = Integer.parseInt(bookingRequestSecondLine[2]);
	        	
	        	bookingRequests.add(BookingRequest.builder()
	        			.employeeId(empId)
	        			.meetingDate(meetingDate)
	        			.meetingDuration(duration)
	        			.meetingStartTime(meetingTime)
	        			.meetingEndTime(meetingTime.plusHours(duration))
	        			.requestTime(requestTime)
	        			.build()
	        		);
	        }
	        
	        return MeetingRequest.builder()
	        		.bookingRequests(bookingRequests)
	        		.officeStartTime(officeStartTime)
	        		.officeEndTime(officeEndTime)
	        		.build();
		}
		catch(IOException e) {
			log.error("Error while parsing input file ", e);
			throw new MeetingsFileNotValidException("Error while parsing input file " + e.getMessage());
		}		
	}
	
	private static LocalDateTime getLocalDateTimeFromString(String localDateTime, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format); 
		return LocalDateTime.parse(localDateTime, formatter);

		
	}
	
	private static LocalTime getTimeFromString(String timeInString) {
		return LocalTime
				.of(
						Integer.parseInt(timeInString.substring(0, 2)), 
						Integer.parseInt(timeInString.substring(2))
					);
	}
}
