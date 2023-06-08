package com.systa.meetings.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.systa.meetings.domain.BookingRequest;
import com.systa.meetings.domain.MeetingRequest;
import com.systa.meetings.exceptions.MeetingsFileNotValidException;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
	
	@InjectMocks
	private FileService fileService;
	
	@Test
	@DisplayName("It will process and will return all meetings from file")
	void test_getMeetingRequestData_one() {
		String bookingRequestsToProcess = "0900 1730\n"
				+ "2020-01-18 10:17:06 EMP001\n"
				+ "2020-01-21 09:00 2\n"
				+ "2020-01-18 12:34:56 EMP002\n"
				+ "2020-01-21 09:00 2\n"
				+ "2020-01-18 09:28:23 EMP003\n"
				+ "2020-01-22 14:00 2\n"
				+ "2020-01-18 11:23:45 EMP004\n"
				+ "2020-01-22 16:00 1\n"
				+ "2020-01-15 17:29:12 EMP005\n"
				+ "2020-01-21 16:00 3\n"
				+ "2020-01-18 11:00:45 EMP006\n"
				+ "2020-01-23 16:00 1\n"
				+ "2020-01-15 11:00:45 EMP007\n"
				+ "2020-01-23 15:00 2";
		
		MockMultipartFile file = new MockMultipartFile("file", "meetings.txt", "text/plain", bookingRequestsToProcess.getBytes());
		
		MeetingRequest result = fileService.getMeetingRequestData(file);
		
		assertEquals(LocalTime.of(9, 00), result.getOfficeStartTime());
		assertEquals(LocalTime.of(17, 30), result.getOfficeEndTime());
		
		assertEquals(7, result.getBookingRequests().size());
		
		int i = 1;
		
		for(BookingRequest request : result.getBookingRequests()) {
			if (i == 1) {
				assertEquals("EMP007", request.getEmployeeId());
			}
			else if (i == 2) {
				assertEquals("EMP005", request.getEmployeeId());
			}
			else if (i == 3) {
				assertEquals("EMP003", request.getEmployeeId());
			}
			else if (i == 4) {
				assertEquals("EMP001", request.getEmployeeId());
			}
			else if (i == 5) {
				assertEquals("EMP006", request.getEmployeeId());
			}
			else if (i == 6) {
				assertEquals("EMP004", request.getEmployeeId());
			}
			else if (i == 7) {
				assertEquals("EMP002", request.getEmployeeId());
			}
			i++;
		}
		
	}
	
	@Test
	@DisplayName("It will fail as file is empty")
	void test_getMeetingRequestData_fail_asFileIsEmpty() {
		String bookingRequestsToProcess = "";
		
		MockMultipartFile file = new MockMultipartFile("file", "meetings.txt", "text/plain", bookingRequestsToProcess.getBytes());
		
		MeetingsFileNotValidException error = Assertions.assertThrows(MeetingsFileNotValidException.class, () -> {
			fileService.getMeetingRequestData(file);
		});
		
		assertEquals("File is empty", error.getMessage());		
	}
	
	@Test
	@DisplayName("It will fail as Meeting time is not present in file")
	void test_getMeetingRequestData_fail_asMeetingTimeIsNotPresent() {
		String bookingRequestsToProcess = "0900 1730\n"
				+ "2020-01-18 10:17:06 EMP001";
		
		MockMultipartFile file = new MockMultipartFile("file", "meetings.txt", "text/plain", bookingRequestsToProcess.getBytes());
		
		MeetingsFileNotValidException error = Assertions.assertThrows(MeetingsFileNotValidException.class, () -> {
			fileService.getMeetingRequestData(file);
		});
		
		assertEquals("Data is not correct", error.getMessage());		
	}

}
