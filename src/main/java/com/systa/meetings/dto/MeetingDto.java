/**
 * 
 */
package com.systa.meetings.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * @author mohsin
 *
 */

@Data
@Builder
public class MeetingDto implements Comparable<MeetingDto>{

	@JsonProperty("emp_id")
	private String employeeId;
	
	@JsonProperty("start_time")
	private LocalTime startTime;
	
	@JsonProperty("end_time")
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
