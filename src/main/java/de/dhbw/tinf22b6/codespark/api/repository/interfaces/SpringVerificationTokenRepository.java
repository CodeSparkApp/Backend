package de.dhbw.tinf22b6.codespark.api.repository.interfaces;

import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import de.dhbw.tinf22b6.codespark.api.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringVerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
	Optional<VerificationToken> findByTokenAndType(String token, VerificationTokenType type);
}
