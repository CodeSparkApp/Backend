package de.dhbw.tinf22b6.codespark.api.mapper;

import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;

public class AccountMapper {
	private AccountMapper() {}

	public static Account mapAccountCreateRequestToAccount(AccountCreateRequest accountCreateRequest) {
		return new Account(
				accountCreateRequest.getUsername(),
				accountCreateRequest.getEmail(),
				accountCreateRequest.getPassword(),
				UserRoleType.USER,
				false,
				null
		);
	}
}
