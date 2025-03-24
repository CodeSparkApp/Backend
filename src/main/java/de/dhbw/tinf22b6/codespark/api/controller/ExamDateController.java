package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.ExamDateUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ExamDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/exam-date")
public class ExamDateController {
	private final ExamDateService examDateService;

	public ExamDateController(@Autowired ExamDateService examDateService) {
		this.examDateService = examDateService;
	}

	@PostMapping("/update")
	public ResponseEntity<Void> updateExamDate(@RequestBody ExamDateUpdateRequest examDateUpdateRequest,
											   @AuthenticationPrincipal Account account) {
		examDateService.updateExamDate(examDateUpdateRequest, account);
		return ResponseEntity.ok().build();
	}
}
