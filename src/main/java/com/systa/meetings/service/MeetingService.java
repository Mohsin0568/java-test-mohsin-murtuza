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

	/**
	 * 
	 * @param file
	 * @return ConfirmedMeetings order by date chronologically
	 * 
	 * This method will process file, find valid meetings and will confirm bookings 
	 */
	public Set<ConfirmedMeetingsDto> processBookingRequests(MultipartFile file) {	
		
		
		MeetingRequest request = null;
		request = fileService.getMeetingRequestData(file);
		
		if(request == null) {
			throw new MeetingsFileNotValidException("Some problem with File, unable to process it");
		}
		
		Map<LocalDate, List<BookingRequest>> confirmedBookingRequestsMap = getConfirmedBookingsGroupByDate(request);
		
		return getConfirmedMeetingsDtoObject(confirmedBookingRequestsMap);
		
	}
	
	
	/**
	 * 
	 * @param request
	 * @return getConfirmedBookingsGroupByDate
	 * 
	 * This method will take all bookings, filters out meetings which are outside office timings also filters out conflicts meetings and will return confirmed bookings
	 */
	private Map<LocalDate, List<BookingRequest>> getConfirmedBookingsGroupByDate(MeetingRequest request){
		
		Map<LocalDate, List<BookingRequest>> confirmedBookingRequestsMap = new HashMap<>();
		
		final LocalTime officeStartTime = request.getOfficeStartTime();
		final LocalTime officeEndTime = request.getOfficeEndTime();		
		
		// this predicate will check if meeting request start time is after office hours.
		Predicate<BookingRequest> startTimePredicate = bookingRequest -> !(bookingRequest.getMeetingStartTime().isAfter(officeEndTime) 
																							|| bookingRequest.getMeetingStartTime().isBefore(officeStartTime));
		
		// this predicate will check if meeting request end time is after office hours.
		Predicate<BookingRequest> endTimePredicate = bookingRequest -> !(bookingRequest.getMeetingEndTime().isAfter(officeEndTime) 
				|| bookingRequest.getMeetingEndTime().isBefore(officeStartTime));
		
		
		// will iterate each booking request and will first filter out request which have meeting time after office hours.
		request.getBookingRequests().stream().filter(startTimePredicate.and(endTimePredicate)).forEach(bookingRequest -> {
			
			List<BookingRequest> bookingRequestList = confirmedBookingRequestsMap.get(bookingRequest.getMeetingDate());
			if(null == bookingRequestList) { // if no booking on given day, then directly add new booking reques to list
				bookingRequestList = new ArrayList<>();
				bookingRequestList.add(bookingRequest);
				
				confirmedBookingRequestsMap.put(bookingRequest.getMeetingDate(), bookingRequestList);
			}
			else if(!isMeetingConflicting(bookingRequestList, bookingRequest)){ // if booking request list is not empty for a day, then check for meeting conflicts before adding new request
				bookingRequestList.add(bookingRequest);
			}
			else {
				log.info("This meeting request for employeeId {} for time {} is conflicting, so ignoring", bookingRequest.getEmployeeId(), bookingRequest.getMeetingStartTime());
			}
			
		});
		
		return confirmedBookingRequestsMap;
		
	}
	
	
	/**
	 * 
	 * @param confirmedBookingRequestsMap
	 * @return ConfirmedMeetingsDto
	 * 
	 * This method will convert MeetingRequests object to Dto also will have confirm meetings in chronologically by day order
	 */
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
	
	/**
	 * 
	 * @param bookingRequests
	 * @param newBookingRequest
	 * @return
	 * 
	 * This method will check if new meeting is having conflicts with other meeting in the same day
	 */
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
		
		// this object will have the conflicting meeting, so if below object is not empty means there is a meeting conflict
		Optional<BookingRequest> conflictingRequest = bookingRequests.stream().filter(startTimePredicate.or(endTimePredicate).or(aroundTimePredicate)).findAny();
		
		return conflictingRequest.isPresent();
	}	
	
}
