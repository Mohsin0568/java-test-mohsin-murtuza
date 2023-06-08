/**
 * 
 */
package com.systa.meetings.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author mohsin
 *
 */

@Data
@Builder
@ToString
public class BookingRequest implements Comparable<BookingRequest>{

	private LocalDateTime requestTime;
	private String employeeId;
	private LocalDate meetingDate;
	private LocalTime meetingStartTime;
	private LocalTime meetingEndTime;
	private int meetingDuration;
	
	@Override
	public int compareTo(BookingRequest o) {
		if(o.getRequestTime().isAfter(this.getRequestTime()))
			return -1;
		else
			return 1;
	}
	
}
