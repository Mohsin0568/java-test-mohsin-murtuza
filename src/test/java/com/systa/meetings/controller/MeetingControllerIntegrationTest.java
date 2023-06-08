package com.systa.meetings.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class MeetingControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@DisplayName("Process booking requests - Successful")
	void processFile_successful() throws Exception {
		
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
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/meetings/process")
				.file(file))
				.andExpect(status().isOk())
				.andReturn();
	}
	
	@Test
	@DisplayName("Process booking requests - When file is empty, should throw error")
	void processFile_give400_whenFileIsEmpty() throws Exception {
		
		String bookingRequestsToProcess = "";
		
		MockMultipartFile file = new MockMultipartFile("file", "meetings.txt", "text/plain", bookingRequestsToProcess.getBytes());
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/meetings/process")
				.file(file))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("File is empty")));
	}
	
	@Test
	@DisplayName("Process booking requests - when booking details structure is not correct, should throw error")
	void processFile_give400_whenFileDoesNotHaveBookingDate() throws Exception {
		
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
				+ "2020-01-15 11:00:45 EMP007";
		
		MockMultipartFile file = new MockMultipartFile("file", "meetings.txt", "text/plain", bookingRequestsToProcess.getBytes());
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/meetings/process")
				.file(file))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Data is not correct")));
	}


}
