package com.systa.meetings.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.systa.meetings.domain.BookingRequest;
import com.systa.meetings.domain.MeetingRequest;
import com.systa.meetings.dto.ConfirmedMeetingsDto;
import com.systa.meetings.dto.MeetingDto;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {
	
	@Mock
	private FileService fileService;
	
	@InjectMocks
	private MeetingService meetingService;
	
	@Test
	@DisplayName("Test processBookingRequests when input has 5 meeting requests on same day with 1 conflict and 1 out of office timing")
	void test_processBookingRequests_one() {
		
		MockMultipartFile file = new MockMultipartFile("file", "meetings.txt", "text/plain", "".getBytes());
		
		when(fileService.getMeetingRequestData(file)).thenReturn(getMeetingRequestWithOneDayMeetingsWithOneConflictAndOneOutOfOfficeTime());
		
		TreeSet<ConfirmedMeetingsDto> result = meetingService.processBookingRequests(file);
		
		assertEquals(1, result.size());
		assertEquals(3, result.first().getBookings().size());
		
		int i = 1;
		
		for(MeetingDto meetingDto : result.first().getBookings()) {
			if(i == 1) {
				assertEquals("EM001", meetingDto.getEmployeeId());
			}
			else if (i == 2) {
				assertEquals("EM002", meetingDto.getEmployeeId());
			}
			else {
				assertEquals("EM004", meetingDto.getEmployeeId());
			}
			
			i++;
		}
	}
	
	@Test
	@DisplayName("Test processBookingRequests when input has 5 meetings for two days with 1 conflict and 1 out of office timing")
	void test_processBookingRequests_two() {
		
		MockMultipartFile file = new MockMultipartFile("file", "meetings.txt", "text/plain", "".getBytes());
		
		when(fileService.getMeetingRequestData(file)).thenReturn(getMeetingRequestForTwoDaysMeetingWithOneConflictAndOneOutOfOfficeTime());
		
		TreeSet<ConfirmedMeetingsDto> result = meetingService.processBookingRequests(file);
		
		assertEquals(2, result.size());
		
		
		Set<MeetingDto> firstDayMeetings = result.pollFirst().getBookings();
		Set<MeetingDto> secondDayMeetings = result.pollFirst().getBookings();
		
		assertEquals(2, firstDayMeetings.size());
		assertEquals(1, secondDayMeetings.size());
 		
		int i = 1;
		
		for(MeetingDto meetingDto : firstDayMeetings) {
			
			if(i == 1) {
				assertEquals("EM001", meetingDto.getEmployeeId());
			}
			else {
				assertEquals("EM002", meetingDto.getEmployeeId());
			}			
			i++;
		}
		
		i = 1;
		for(MeetingDto meetingDto : secondDayMeetings) {
			
			assertEquals("EM003", meetingDto.getEmployeeId());
			
		}
	}
	
	private MeetingRequest getMeetingRequestForTwoDaysMeetingWithOneConflictAndOneOutOfOfficeTime() {
		
		BookingRequest bookingRequest1 = BookingRequest.builder()
				.employeeId("EM001")
				.meetingDate(LocalDate.of(2023, 06, 01))
				.meetingStartTime(LocalTime.of(9, 00))
				.meetingEndTime(LocalTime.of(11, 00))
				.meetingDuration(2)
				.requestTime(LocalDateTime.of(2023, 6, 1, 10, 05, 30))
				.build();
		
		BookingRequest bookingRequest2 = BookingRequest.builder()
				.employeeId("EM002")
				.meetingDate(LocalDate.of(2023, 06, 01))
				.meetingStartTime(LocalTime.of(11, 00))
				.meetingEndTime(LocalTime.of(12, 00))
				.meetingDuration(2)
				.requestTime(LocalDateTime.of(2023, 5, 30, 10, 05, 30))
				.build();
		
		BookingRequest bookingRequest3 = BookingRequest.builder()
				.employeeId("EM003")
				.meetingDate(LocalDate.of(2023, 07, 01))
				.meetingStartTime(LocalTime.of(11, 00))
				.meetingEndTime(LocalTime.of(13, 00))
				.meetingDuration(2)
				.requestTime(LocalDateTime.of(2023, 6, 1, 11, 05, 30))
				.build();
		
		BookingRequest bookingRequest4 = BookingRequest.builder()
				.employeeId("EM004")
				.meetingDate(LocalDate.of(2023, 07, 01))
				.meetingStartTime(LocalTime.of(12, 00))
				.meetingEndTime(LocalTime.of(16, 00))
				.meetingDuration(2)
				.requestTime(LocalDateTime.of(2023, 6, 1, 12, 05, 30))
				.build();
		
		BookingRequest bookingRequest5 = BookingRequest.builder()
				.employeeId("EM005")
				.meetingDate(LocalDate.of(2023, 07, 01))
				.meetingStartTime(LocalTime.of(17, 00))
				.meetingEndTime(LocalTime.of(18, 00))
				.meetingDuration(1)
				.requestTime(LocalDateTime.of(2023, 6, 1, 13, 05, 30))
				.build();
			
		Set<BookingRequest> bookingRequests = new TreeSet<>();
		bookingRequests.add(bookingRequest1);
		bookingRequests.add(bookingRequest2);
		bookingRequests.add(bookingRequest3);
		bookingRequests.add(bookingRequest4);
		bookingRequests.add(bookingRequest5);
		
		return MeetingRequest.builder()
			.bookingRequests(bookingRequests)
			.officeStartTime(LocalTime.of(9, 0))
			.officeEndTime(LocalTime.of(17, 0))
			.build();
		
	}
	
	private MeetingRequest getMeetingRequestWithOneDayMeetingsWithOneConflictAndOneOutOfOfficeTime() {
		
		BookingRequest bookingRequest1 = BookingRequest.builder()
				.employeeId("EM001")
				.meetingDate(LocalDate.of(2023, 06, 01))
				.meetingStartTime(LocalTime.of(9, 00))
				.meetingEndTime(LocalTime.of(11, 00))
				.meetingDuration(2)
				.requestTime(LocalDateTime.of(2023, 6, 1, 10, 05, 30))
				.build();
		
		BookingRequest bookingRequest2 = BookingRequest.builder()
				.employeeId("EM002")
				.meetingDate(LocalDate.of(2023, 06, 01))
				.meetingStartTime(LocalTime.of(11, 00))
				.meetingEndTime(LocalTime.of(12, 00))
				.meetingDuration(2)
				.requestTime(LocalDateTime.of(2023, 5, 30, 10, 05, 30))
				.build();
		
		BookingRequest bookingRequest3 = BookingRequest.builder()
				.employeeId("EM003")
				.meetingDate(LocalDate.of(2023, 06, 01))
				.meetingStartTime(LocalTime.of(11, 00))
				.meetingEndTime(LocalTime.of(13, 00))
				.meetingDuration(2)
				.requestTime(LocalDateTime.of(2023, 6, 1, 11, 05, 30))
				.build();
		
		BookingRequest bookingRequest4 = BookingRequest.builder()
				.employeeId("EM004")
				.meetingDate(LocalDate.of(2023, 06, 01))
				.meetingStartTime(LocalTime.of(14, 00))
				.meetingEndTime(LocalTime.of(16, 00))
				.meetingDuration(2)
				.requestTime(LocalDateTime.of(2023, 6, 1, 12, 05, 30))
				.build();
		
		BookingRequest bookingRequest5 = BookingRequest.builder()
				.employeeId("EM005")
				.meetingDate(LocalDate.of(2023, 06, 01))
				.meetingStartTime(LocalTime.of(17, 00))
				.meetingEndTime(LocalTime.of(18, 00))
				.meetingDuration(1)
				.requestTime(LocalDateTime.of(2023, 6, 1, 13, 05, 30))
				.build();
			
		Set<BookingRequest> bookingRequests = new TreeSet<>();
		bookingRequests.add(bookingRequest1);
		bookingRequests.add(bookingRequest2);
		bookingRequests.add(bookingRequest3);
		bookingRequests.add(bookingRequest4);
		bookingRequests.add(bookingRequest5);
		
		return MeetingRequest.builder()
			.bookingRequests(bookingRequests)
			.officeStartTime(LocalTime.of(9, 0))
			.officeEndTime(LocalTime.of(17, 0))
			.build();
		
	}

}
