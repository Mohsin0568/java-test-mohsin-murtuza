/**
 * 
 */
package com.systa.meetings.domain;

import java.time.LocalTime;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

/**
 * @author mohsin
 *
 */

@Data
@Builder
public class MeetingRequest {

	LocalTime officeStartTime;
	LocalTime officeEndTime;
	
	Set<BookingRequest> bookingRequests;
}
