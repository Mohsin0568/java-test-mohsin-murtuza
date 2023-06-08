/**
 * 
 */
package com.systa.meetings.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.systa.meetings.service.MeetingService;

/**
 * @author mohsin
 *
 */

@RestController
@RequestMapping("/v1/meetings")
public class MeetingController {
	
	@Autowired
	MeetingService meetingService;
	
	@PostMapping("/process")
	public void getMeetings(@RequestParam("file") MultipartFile meetingsFile) {
		meetingService.processBookingRequests(meetingsFile);
	}

}
