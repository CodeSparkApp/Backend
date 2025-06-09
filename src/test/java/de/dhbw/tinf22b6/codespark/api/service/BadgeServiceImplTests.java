package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.BadgeType;
import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Badge;
import de.dhbw.tinf22b6.codespark.api.model.UserBadge;
import de.dhbw.tinf22b6.codespark.api.model.UserLessonProgress;
import de.dhbw.tinf22b6.codespark.api.payload.response.BadgeItemResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.BadgesOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.repository.*;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.BadgeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BadgeServiceImplTests {
	private SimpMessagingTemplate messagingTemplate;
	private BadgeRepository badgeRepository;
	private UserBadgeRepository userBadgeRepository;
	private UserLessonProgressRepository userLessonProgressRepository;
	private ChapterProgressRepository chapterProgressRepository;
	private ChapterRepository chapterRepository;
	private BadgeService badgeService;

	@BeforeEach
	void setup() {
		messagingTemplate = mock(SimpMessagingTemplate.class);
		badgeRepository = mock(BadgeRepository.class);
		userBadgeRepository = mock(UserBadgeRepository.class);
		userLessonProgressRepository = mock(UserLessonProgressRepository.class);
		chapterProgressRepository = mock(ChapterProgressRepository.class);
		chapterRepository = mock(ChapterRepository.class);

		badgeService = new BadgeServiceImpl(
				messagingTemplate, badgeRepository, userBadgeRepository,
				userLessonProgressRepository, chapterProgressRepository, chapterRepository
		);
	}

	@Test
	void getBadgesOverview_shouldReturnAllBadgesWithOptionalReceiveDates() {
		Account account = new Account();
		UUID badgeId = UUID.randomUUID();
		Badge badge = new Badge("Badge 1", "Desc", BadgeType.FIRST_LESSON_COMPLETED, "icon");
		badge.setId(badgeId);

		when(badgeRepository.findAll()).thenReturn(List.of(badge));
		when(userBadgeRepository.findByAccountAndBadge(account, badge))
				.thenReturn(Optional.of(new UserBadge(account, badge, LocalDateTime.of(2024, 1, 1, 0, 0))));

		BadgesOverviewResponse result = badgeService.getBadgesOverview(account);

		assertThat(result.getBadges()).hasSize(1);
		BadgeItemResponse item = result.getBadges().get(0);
		assertThat(item.getId()).isEqualTo(badgeId);
		assertThat(item.getReceiveDate()).isNotNull();
	}

	@Test
	@Transactional
	void checkAndAssignBadges_shouldAssignBadgesIfEligible() {
		Account account = new Account();
		account.setUsername("test");

		UUID badgeId = UUID.randomUUID();
		Badge badge = new Badge("Badge 1", "Desc", BadgeType.FIRST_LESSON_COMPLETED, "icon");
		badge.setId(badgeId);

		when(userLessonProgressRepository.findByAccountAndState(account, LessonProgressState.SOLVED))
				.thenReturn(List.of(new UserLessonProgress()));
		when(chapterProgressRepository.findProgressByAccountId(any())).thenReturn(List.of());
		when(badgeRepository.findByType(BadgeType.FIRST_LESSON_COMPLETED)).thenReturn(Optional.of(badge));
		when(userBadgeRepository.existsByAccountAndBadge(account, badge)).thenReturn(false);

		badgeService.checkAndAssignBadges(account);

		verify(userBadgeRepository).save(any(UserBadge.class));
		verify(messagingTemplate).convertAndSendToUser(eq("test"), eq("/queue/badges"), any());
	}

	@Test
	void checkAndAssignBadges_shouldSkipIfNoProgress() {
		Account account = new Account();
		when(userLessonProgressRepository.findByAccountAndState(account, LessonProgressState.SOLVED)).thenReturn(List.of());
		when(chapterProgressRepository.findProgressByAccountId(account.getId())).thenReturn(List.of());

		badgeService.checkAndAssignBadges(account);

		verify(userBadgeRepository, never()).save(any());
		verify(messagingTemplate, never()).convertAndSendToUser(any(), any(), any());
	}

	@Test
	void checkAndAssignBadges_shouldHandleMissingBadgeGracefully() {
		Account account = new Account();
		when(userLessonProgressRepository.findByAccountAndState(account, LessonProgressState.SOLVED)).thenReturn(List.of(new UserLessonProgress()));
		when(chapterProgressRepository.findProgressByAccountId(account.getId())).thenReturn(List.of());
		when(badgeRepository.findByType(BadgeType.FIRST_LESSON_COMPLETED)).thenReturn(Optional.empty());

		badgeService.checkAndAssignBadges(account);

		verify(userBadgeRepository, never()).save(any());
		verify(messagingTemplate, never()).convertAndSendToUser(any(), any(), any());
	}

	@Test
	void checkAndAssignBadges_shouldHandleAlreadyEarnedBadge() {
		Account account = new Account();
		Badge badge = new Badge("Badge", "desc", BadgeType.FIRST_LESSON_COMPLETED, "icon");
		when(userLessonProgressRepository.findByAccountAndState(account, LessonProgressState.SOLVED)).thenReturn(List.of(new UserLessonProgress()));
		when(chapterProgressRepository.findProgressByAccountId(account.getId())).thenReturn(List.of());
		when(badgeRepository.findByType(BadgeType.FIRST_LESSON_COMPLETED)).thenReturn(Optional.of(badge));
		when(userBadgeRepository.existsByAccountAndBadge(account, badge)).thenReturn(true);

		badgeService.checkAndAssignBadges(account);

		verify(userBadgeRepository, never()).save(any());
		verify(messagingTemplate, never()).convertAndSendToUser(any(), any(), any());
	}
}
