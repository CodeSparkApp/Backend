package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Badge;
import de.dhbw.tinf22b6.codespark.api.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {
	Optional<UserBadge> findByAccountAndBadge(Account account, Badge badge);
	boolean existsByAccountAndBadge(Account account, Badge badge);
}
