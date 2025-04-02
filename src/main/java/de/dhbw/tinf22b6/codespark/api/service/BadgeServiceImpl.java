package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.BadgeType;
import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Badge;
import de.dhbw.tinf22b6.codespark.api.model.UserBadge;
import de.dhbw.tinf22b6.codespark.api.model.UserLessonProgress;
import de.dhbw.tinf22b6.codespark.api.payload.response.BadgeEarnResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.BadgeItemResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.BadgesOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.projection.ChapterProgressProjection;
import de.dhbw.tinf22b6.codespark.api.repository.*;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class BadgeServiceImpl implements BadgeService {
	private final SimpMessagingTemplate messagingTemplate;
	private final BadgeRepository badgeRepository;
	private final UserBadgeRepository userBadgeRepository;
	private final UserLessonProgressRepository userLessonProgressRepository;
	private final ChapterProgressRepository chapterProgressRepository;
	private final ChapterRepository chapterRepository;

	private final Object badgeLock = new Object();

	public BadgeServiceImpl(@Autowired SimpMessagingTemplate messagingTemplate,
							@Autowired BadgeRepository badgeRepository,
							@Autowired UserBadgeRepository userBadgeRepository,
							@Autowired UserLessonProgressRepository userLessonProgressRepository,
							@Autowired ChapterProgressRepository chapterProgressRepository,
							@Autowired ChapterRepository chapterRepository) {
		this.messagingTemplate = messagingTemplate;
		this.badgeRepository = badgeRepository;
		this.userBadgeRepository = userBadgeRepository;
		this.userLessonProgressRepository = userLessonProgressRepository;
		this.chapterProgressRepository = chapterProgressRepository;
		this.chapterRepository = chapterRepository;
	}

	@Override
	public BadgesOverviewResponse getBadgesOverview(Account account) {
		List<Badge> badges = badgeRepository.findAll();

		BadgesOverviewResponse response = new BadgesOverviewResponse();
		response.setBadges(
				badges.stream()
						.map(badge -> {
							LocalDateTime receiveDate = userBadgeRepository
									.findByAccountAndBadge(account, badge)
									.map(UserBadge::getReceiveDate)
									.orElse(null);

							return new BadgeItemResponse(
									badge.getId(),
									badge.getName(),
									badge.getDescription(),
									badge.getIcon(),
									receiveDate
							);
						})
						.collect(Collectors.toList())
		);

		return response;
	}

	@Async("BadgeExecutor")
	@Transactional
	@Override
	public void checkAndAssignBadges(Account account) {
		List<Badge> earnedBadges = new ArrayList<>();

		List<UserLessonProgress> completedLessons = userLessonProgressRepository.findByAccountAndState(account, LessonProgressState.SOLVED);
		List<ChapterProgressProjection> chapterProgressList = chapterProgressRepository.findProgressByAccountId(account.getId());

		assignBadgeIfEligible(earnedBadges, account, BadgeType.FIRST_LESSON_COMPLETED,
				() -> !completedLessons.isEmpty()
		);
		assignBadgeIfEligible(earnedBadges, account, BadgeType.FIRST_CHAPTER_COMPLETED,
				() -> chapterProgressList.stream()
						.anyMatch(c -> c.getProgress() >= 1.0f)
		);
		assignBadgeIfEligible(earnedBadges, account, BadgeType.ALL_CHAPTERS_COMPLETED,
				() -> {
					Map<UUID, Float> progressMap = chapterProgressList.stream()
							.collect(Collectors.toMap(ChapterProgressProjection::getChapterId, ChapterProgressProjection::getProgress));

					return chapterRepository.findAll().stream()
							.allMatch(c -> progressMap.getOrDefault(c.getId(), 0.0f) >= 1.0f);
				}
		);

		earnedBadges.stream()
				.map(b -> new BadgeEarnResponse(
						b.getId(),
						b.getName(),
						b.getDescription(),
						b.getIcon()))
				.forEach(b -> sendBadgeNotification(account, b));
	}

	private void assignBadgeIfEligible(List<Badge> earnedBadges, Account account, BadgeType badgeType, Supplier<Boolean> condition) {
		if (!condition.get()) {
			return;
		}

		synchronized (badgeLock) {
			badgeRepository.findByType(badgeType).ifPresent(badge -> {
				if (!userBadgeRepository.existsByAccountAndBadge(account, badge)) {
					UserBadge newUserBadge = new UserBadge(account, badge, LocalDateTime.now());
					userBadgeRepository.save(newUserBadge);
					earnedBadges.add(badge);
				}
			});
			// TODO: Else log error
		}
	}

	private void sendBadgeNotification(Account account, BadgeEarnResponse response) {
		messagingTemplate.convertAndSendToUser(account.getUsername(), "/queue/badges", response);
	}
}

