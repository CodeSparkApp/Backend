package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/account")
public class AccountController {
	private final AccountService accountService;

	public AccountController(@Autowired AccountService accountService) {
		this.accountService = accountService;
	}
}
