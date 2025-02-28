package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import de.dhbw.tinf22b6.codespark.api.model.VerificationToken;
import de.dhbw.tinf22b6.codespark.api.repository.interfaces.SpringVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VerificationTokenRepository {
	private final SpringVerificationTokenRepository verificationTokenRepository;

	public VerificationTokenRepository(@Autowired SpringVerificationTokenRepository verificationTokenRepository) {
		this.verificationTokenRepository = verificationTokenRepository;
	}

	public VerificationToken save(VerificationToken verificationToken) {
		return verificationTokenRepository.save(verificationToken);
	}

	public void delete(VerificationToken verificationToken) {
		verificationTokenRepository.delete(verificationToken);
	}

	public Optional<VerificationToken> findByTokenAndType(String token, VerificationTokenType type) {
		return verificationTokenRepository.findByTokenAndType(token, type);
	}
}
