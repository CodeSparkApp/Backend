package de.dhbw.tinf22b6.codespark.api.projection;

import java.util.UUID;

public interface ChapterProgressProjection {
	UUID getChapterId();
	float getProgress();
}
