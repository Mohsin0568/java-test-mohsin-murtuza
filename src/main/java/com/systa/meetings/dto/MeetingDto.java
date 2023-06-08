/**
 * 
 */
package com.systa.meetings.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Builder;
import lombok.Data;

/**
 * @author mohsin
 *
 */

@Data
@Builder
public class MeetingDto implements Comparable<MeetingDto>{

	@JsonAlias("emp_id")
	private String employeeId;
	
	@JsonAlias("start_time")
	private LocalTime startTime;
	
	@JsonAlias("end_time")
	private LocalTime endTime;

	@Override
	public int compareTo(MeetingDto o) {
		if(o.getStartTime().isAfter(this.getStartTime())) {
			return -1;
		}
		else {
			return 1;
		}
				
	}
}
