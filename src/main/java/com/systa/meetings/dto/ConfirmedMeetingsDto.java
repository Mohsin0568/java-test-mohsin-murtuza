/**
 * 
 */
package com.systa.meetings.dto;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Builder;
import lombok.Data;

/**
 * @author mohsin
 *
 */

@Data
@Builder
public class ConfirmedMeetingsDto implements Comparable<ConfirmedMeetingsDto>{

	@JsonAlias("data")
	private LocalDate date;
	
	private Set<MeetingDto> bookings;

	@Override
	public int compareTo(ConfirmedMeetingsDto o) {
		if(o.getDate().isAfter(this.getDate())) {
			return -1;
		}			
		else {
			return 1;
		}			
	}	
}
