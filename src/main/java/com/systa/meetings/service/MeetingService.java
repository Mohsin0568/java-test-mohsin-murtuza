/**
 * 
 */
package com.systa.meetings.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.systa.meetings.domain.BookingRequest;
import com.systa.meetings.domain.MeetingRequest;
import com.systa.meetings.dto.ConfirmedMeetingsDto;
import com.systa.meetings.dto.MeetingDto;
import com.systa.meetings.exceptions.MeetingsFileNotValidException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mohsin
 *
 */

@Service
@Slf4j
public class MeetingService {
	
	@Autowired
	FileService fileService;

	public Set<ConfirmedMeetingsDto> processBookingRequests(MultipartFile file) {	
		
		
		MeetingRequest request = null;
		request = fileService.getMeetingRequestData(file);
		
		if(request == null) {
			throw new MeetingsFileNotValidException("Some problem with File, unable to process it");
		}
		
		Map<LocalDate, List<BookingRequest>> confirmedBookingRequestsMap = new HashMap<>();
		
		
		final LocalTime officeStartTime = request.getOfficeStartTime();
		final LocalTime officeEndTime = request.getOfficeEndTime();		
		
		Predicate<BookingRequest> startTimePredicate = bookingRequest -> !(bookingRequest.getMeetingStartTime().isAfter(officeEndTime) 
																							|| bookingRequest.getMeetingStartTime().isBefore(officeStartTime));
		
		Predicate<BookingRequest> endTimePredicate = bookingRequest -> !(bookingRequest.getMeetingEndTime().isAfter(officeEndTime) 
				|| bookingRequest.getMeetingEndTime().isBefore(officeStartTime));
		
		
		// will iterate each booking request and will first filter out request which have meeting time after office hours.
		request.getBookingRequests().stream().filter(startTimePredicate.and(endTimePredicate)).forEach(bookingRequest -> {
			
			List<BookingRequest> bookingRequestList = confirmedBookingRequestsMap.get(bookingRequest.getMeetingDate());
			if(null == bookingRequestList) {
				bookingRequestList = new ArrayList<>();
				bookingRequestList.add(bookingRequest);
				
				confirmedBookingRequestsMap.put(bookingRequest.getMeetingDate(), bookingRequestList);
			}
			else if(!isMeetingConflicting(bookingRequestList, bookingRequest)){
				bookingRequestList.add(bookingRequest);
			}
			else {
				log.info("This meeting request for employeeId {} for time {} is conflicting, so ignoring", bookingRequest.getEmployeeId(), bookingRequest.getMeetingStartTime());
			}
			
		});
		
		return getConfirmedMeetingsDtoObject(confirmedBookingRequestsMap);
		
	}
	
	private Set<ConfirmedMeetingsDto> getConfirmedMeetingsDtoObject(Map<LocalDate, List<BookingRequest>> confirmedBookingRequestsMap){
		Set<ConfirmedMeetingsDto> confirmedMeetingsDtoObject = new TreeSet<>();
		
		
		for (Map.Entry<LocalDate, List<BookingRequest>> entry : confirmedBookingRequestsMap.entrySet()) {
			
			Set<MeetingDto> meetingsDto = new TreeSet<>();
			
			entry.getValue().stream().forEach(x -> {
				meetingsDto.add(MeetingDto.builder()
							.employeeId(x.getEmployeeId())
							.startTime(x.getMeetingStartTime())
							.endTime(x.getMeetingEndTime())
							.build()
						);			
			});
			
			confirmedMeetingsDtoObject.add(ConfirmedMeetingsDto.builder()
					.bookings(meetingsDto)
					.date(entry.getKey())
					.build()
				);
	    }
		
		return confirmedMeetingsDtoObject;
		
	}
	
	private boolean isMeetingConflicting(List<BookingRequest> bookingRequests, BookingRequest newBookingRequest) {
		
		log.info("Booking requests {} and new booking request is {}", bookingRequests, newBookingRequest);
		
		Predicate<BookingRequest> startTimePredicate = request -> (newBookingRequest.getMeetingStartTime().isAfter(request.getMeetingStartTime())
																	|| newBookingRequest.getMeetingStartTime().equals(request.getMeetingStartTime()))	
																&& newBookingRequest.getMeetingStartTime().isBefore(request.getMeetingEndTime());	
		
		Predicate<BookingRequest> endTimePredicate = request -> newBookingRequest.getMeetingEndTime().isAfter(request.getMeetingStartTime())																	
																&& (newBookingRequest.getMeetingEndTime().isBefore(request.getMeetingStartTime())
																	|| newBookingRequest.getMeetingEndTime().equals(request.getMeetingStartTime()));
		
		Predicate<BookingRequest> aroundTimePredicate = request -> newBookingRequest.getMeetingStartTime().isBefore(request.getMeetingStartTime())
																&& newBookingRequest.getMeetingEndTime().isAfter(request.getMeetingEndTime());
		
		Optional<BookingRequest> conflictingRequest = bookingRequests.stream().filter(startTimePredicate.or(endTimePredicate).or(aroundTimePredicate)).findAny();
		
		return conflictingRequest.isPresent();
	}	
	
}
