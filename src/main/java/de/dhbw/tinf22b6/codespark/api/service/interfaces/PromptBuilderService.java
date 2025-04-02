package de.dhbw.tinf22b6.codespark.api.service.interfaces;

public interface PromptBuilderService {
	String buildPromptForCodeAnalysis(String question, String sampleSolution, String userAnswer);
	String buildPromptForDebuggingLesson(String faultyCode, String expectedOutput, String sampleSolution, String userAnswer);
	String buildPromptForProgramming(String problem, String sampleSolution, String userAnswer);
}
