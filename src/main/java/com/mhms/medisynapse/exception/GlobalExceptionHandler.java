package com.mhms.medisynapse.exception;

import com.mhms.medisynapse.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("ResourceNotFoundException: {} at path: {}", ex.getMessage(), request.getRequestURI());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle InvalidDataException (400)
     */
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidDataException(
            InvalidDataException ex,
            HttpServletRequest request) {

        log.warn("InvalidDataException: {} at path: {}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    /**
     * Handle BusinessLogicException (422)
     */
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessLogicException(
            BusinessLogicException ex,
            HttpServletRequest request) {

        log.warn("BusinessLogicException: {} at path: {}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    /**
     * Handle IllegalArgumentException (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("IllegalArgumentException: {} at path: {}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    /**
     * Handle MissingServletRequestParameterException (400)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        log.warn("Missing request parameter: {} at path: {}", ex.getParameterName(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message(String.format("Required parameter '%s' is missing", ex.getParameterName()))
                        .data(null)
                        .build()
        );
    }

    /**
     * Handle MethodArgumentTypeMismatchException (400)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("Type mismatch for parameter: {} at path: {}", ex.getName(), request.getRequestURI());

        String message = String.format("Invalid value for parameter '%s'. Expected type: %s",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message(message)
                        .data(null)
                        .build()
        );
    }

    /**
     * Handle MethodArgumentNotValidException (400) - Bean Validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.warn("Validation error at path: {}", request.getRequestURI());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build()
        );
    }

    /**
     * Handle Custom ValidationException (400) - Custom validation errors
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        log.warn("Custom validation error at path: {}", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(ex.getErrors())
                        .build()
        );
    }

    /**
     * Handle BadCredentialsException (401) - Authentication failures
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(
            org.springframework.security.authentication.BadCredentialsException ex,
            HttpServletRequest request) {

        log.warn("Authentication failed at path: {} - Invalid credentials", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Invalid email or password")
                        .data(null)
                        .build()
        );
    }

    /**
     * Handle AuthenticationException (401) - General authentication errors
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex,
            HttpServletRequest request) {

        log.warn("Authentication error at path: {} - {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Authentication failed: " + ex.getMessage())
                        .data(null)
                        .build()
        );
    }

    /**
     * Handle AccessDeniedException (403) - Authorization failures
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied at path: {} - {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Access denied: You don't have permission to access this resource")
                        .data(null)
                        .build()
        );
    }

    /**
     * Handle UsernameNotFoundException (401) - User not found during authentication
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(
            UsernameNotFoundException ex,
            HttpServletRequest request) {

        log.warn("User not found at path: {} - {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Object>builder()
                        .success(false)
                        .message("Invalid email or password")
                        .data(null)
                        .build()
        );
    }

    /**
     * Handle generic Exception (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error at path: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("An unexpected error occurred")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
