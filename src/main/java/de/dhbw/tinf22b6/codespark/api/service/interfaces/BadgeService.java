package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.response.BadgesOverviewResponse;

public interface BadgeService {
	BadgesOverviewResponse getBadgesOverview(Account account);
	void checkAndAssignBadges(Account account);
}
