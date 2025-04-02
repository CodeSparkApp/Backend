package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import java.util.List;

public interface PromptBuilderService {
	String buildPromptForCodeAnalysis(String question, String sampleSolution, String userAnswer);
	String buildPromptForMultipleChoiceEvaluation(String question, List<String> options, List<Integer> correctAnswers,
												  List<Integer> submittedAnswers, boolean isCorrect);
	String buildPromptForFillBlanksEvaluation(String templateCode, String expectedOutput, List<String> correctBlanks,
											  List<String> submittedBlanks, boolean isCorrect);
	String buildPromptForDebuggingLesson(String faultyCode, String expectedOutput, String sampleSolution, String userAnswer);
	String buildPromptForProgramming(String problem, String sampleSolution, String userAnswer);
}
