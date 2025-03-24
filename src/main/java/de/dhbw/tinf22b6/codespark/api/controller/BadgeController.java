package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.response.BadgesOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/badge")
public class BadgeController {
	private final BadgeService badgeService;

	public BadgeController(@Autowired BadgeService badgeService) {
		this.badgeService = badgeService;
	}

	@GetMapping("/overview")
	public ResponseEntity<BadgesOverviewResponse> getBadgesOverview(@AuthenticationPrincipal Account account) {
		BadgesOverviewResponse response = badgeService.getBadgesOverview(account);
		return ResponseEntity.ok().body(response);
	}
}
