package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.repository.interfaces.SpringAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepository {
	private final SpringAccountRepository accountRepository;

	public AccountRepository(@Autowired SpringAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public Account save(Account account) {
		return accountRepository.save(account);
	}

	public Optional<Account> findById(UUID id) {
		return accountRepository.findById(id);
	}

	public Optional<Account> findByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	public Optional<Account> findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}
}
