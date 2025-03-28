package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import de.dhbw.tinf22b6.codespark.api.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
	Optional<VerificationToken> findByTokenAndType(String token, VerificationTokenType type);
}
