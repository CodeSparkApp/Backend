package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.exception.*;
import de.dhbw.tinf22b6.codespark.api.payload.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(AccountAlreadyExistsException.class)
	public ResponseEntity<ApiErrorResponse> handleAccountAlreadyExistsException(AccountAlreadyExistsException e, WebRequest request) {
		return createApiErrorResponse(
				"Account Already Exists",
				HttpStatus.CONFLICT,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(ChapterNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleChapterNotFoundException(ChapterNotFoundException e, WebRequest request) {
		return createApiErrorResponse(
				"Chapter Not Found",
				HttpStatus.NOT_FOUND,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(ChatStreamingException.class)
	public ResponseEntity<ApiErrorResponse> handleChatStreamingException(ChatStreamingException e, WebRequest request) {
		return createApiErrorResponse(
				"Chat Streaming Error",
				HttpStatus.INTERNAL_SERVER_ERROR,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(ImageUploadException.class)
	public ResponseEntity<ApiErrorResponse> handleImageUploadException(ImageUploadException e, WebRequest request) {
		return createApiErrorResponse(
				"Image Upload Error",
				HttpStatus.INTERNAL_SERVER_ERROR,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(InvalidAccountCredentialsException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidAccountCredentialsException(InvalidAccountCredentialsException e, WebRequest request) {
		return createApiErrorResponse(
				"Invalid Account Credentials",
				HttpStatus.UNAUTHORIZED,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e, WebRequest request) {
		return createApiErrorResponse(
				"Invalid Refresh Token",
				HttpStatus.BAD_REQUEST,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(InvalidVerificationTokenException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidVerificationTokenException(InvalidVerificationTokenException e, WebRequest request) {
		return createApiErrorResponse(
				"Invalid Verification Token",
				HttpStatus.BAD_REQUEST,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(MalformedTaskException.class)
	public ResponseEntity<ApiErrorResponse> handleMalformedTaskException(MalformedTaskException e, WebRequest request) {
		return createApiErrorResponse(
				"Malformed Task",
				HttpStatus.NOT_FOUND,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(StreamWritingException.class)
	public ResponseEntity<ApiErrorResponse> handleStreamWritingException(StreamWritingException e, WebRequest request) {
		return createApiErrorResponse(
				"Stream Writing Error",
				HttpStatus.INTERNAL_SERVER_ERROR,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(TaskNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleTaskNotFoundException(TaskNotFoundException e, WebRequest request) {
		return createApiErrorResponse(
				"Task Not Found",
				HttpStatus.NOT_FOUND,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(UnverifiedAccountException.class)
	public ResponseEntity<ApiErrorResponse> handleUnverifiedAccountException(UnverifiedAccountException e, WebRequest request) {
		return createApiErrorResponse(
				"Unverified Account",
				HttpStatus.FORBIDDEN,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException e, WebRequest request) {
		return createApiErrorResponse(
				"User Not Found",
				HttpStatus.NOT_FOUND,
				e.getMessage(),
				request.getDescription(false)
		);
	}

	private ResponseEntity<ApiErrorResponse> createApiErrorResponse(String title, HttpStatus httpStatus, String message, String path) {
		ApiErrorResponse response = new ApiErrorResponse(
				LocalDateTime.now(),
				httpStatus.value(),
				title,
				message,
				path
		);
		return ResponseEntity
				.status(httpStatus)
				.contentType(MediaType.APPLICATION_JSON)
				.body(response);
	}
}
