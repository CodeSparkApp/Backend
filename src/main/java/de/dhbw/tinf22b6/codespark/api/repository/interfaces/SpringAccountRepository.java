package de.dhbw.tinf22b6.codespark.api.repository.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringAccountRepository extends JpaRepository<Account, UUID> {
	Optional<Account> findByUsername(String username);
	Optional<Account> findByEmail(String email);
	Optional<Account> findByVerificationToken(String token);
}
