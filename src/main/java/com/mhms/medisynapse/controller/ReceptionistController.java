package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.constants.SuccessMessages;
import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.dto.CreateReceptionistDto;
import com.mhms.medisynapse.dto.PasswordResetResponseDto;
import com.mhms.medisynapse.dto.ReceptionistPagedResponseDto;
import com.mhms.medisynapse.dto.ReceptionistResponseDto;
import com.mhms.medisynapse.dto.ResetPasswordDto;
import com.mhms.medisynapse.dto.UpdateReceptionistDto;
import com.mhms.medisynapse.exception.ResourceNotFoundException;
import com.mhms.medisynapse.exception.ValidationException;
import com.mhms.medisynapse.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receptionists")
@RequiredArgsConstructor
@Slf4j
public class ReceptionistController {

    private final UserService userService;

    /**
     * Create a new receptionist.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReceptionistResponseDto>> createReceptionist(@Valid @RequestBody CreateReceptionistDto dto) {
        log.info("POST /api/v1/receptionists - Creating receptionist: {}", dto.getEmail());
        try {
            ReceptionistResponseDto created = userService.createReceptionist(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(SuccessMessages.RECEPTIONIST_CREATED, created));
        } catch (ValidationException ve) {
            log.warn("Validation error creating receptionist: {} -> {}", dto.getEmail(), ve.getErrors());
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed: " + ve.getErrors()));
        } catch (ResourceNotFoundException rnfe) {
            log.warn("Resource not found creating receptionist: {} -> {}", dto.getEmail(), rnfe.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating receptionist: {}", dto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create receptionist"));
        }
    }

    /**
     * Update an existing receptionist; can also reactivate by setting status ACTIVE.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReceptionistResponseDto>> updateReceptionist(@PathVariable Long id, @Valid @RequestBody UpdateReceptionistDto dto) {
        log.info("PUT /api/v1/receptionists/{} - Updating receptionist", id);
        try {
            ReceptionistResponseDto updated = userService.updateReceptionist(id, dto);
            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.RECEPTIONIST_UPDATED, updated));
        } catch (ValidationException ve) {
            log.warn("Validation error updating receptionist {}: {}", id, ve.getErrors());
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed: " + ve.getErrors()));
        } catch (ResourceNotFoundException rnfe) {
            log.warn("Receptionist {} not found: {}", id, rnfe.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating receptionist {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update receptionist"));
        }
    }

    /**
     * Reset a receptionist's password.
     */
    @PutMapping(value = "/{id}/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PasswordResetResponseDto>> resetReceptionistPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordDto dto) {
        log.info("PUT /api/v1/receptionists/{}/reset-password - Resetting password", id);
        try {
            PasswordResetResponseDto resp = userService.resetReceptionistPassword(id, dto);
            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.RECEPTIONIST_PASSWORD_RESET, resp));
        } catch (ValidationException ve) {
            log.warn("Validation error resetting password for receptionist {}: {}", id, ve.getErrors());
            return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed: " + ve.getErrors()));
        } catch (ResourceNotFoundException rnfe) {
            log.warn("Receptionist {} not found for password reset: {}", id, rnfe.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error resetting password for receptionist {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to reset password"));
        }
    }

    /**
     * List receptionists with pagination and optional hospital filter.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReceptionistPagedResponseDto>> getReceptionists(@RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size,
                                                                                      @RequestParam(defaultValue = "name") String sortBy,
                                                                                      @RequestParam(defaultValue = "asc") String sortDir,
                                                                                      @RequestParam(required = false) Long hospitalId) {
        log.info("GET /api/v1/receptionists - page: {}, size: {}, sortBy: {}, sortDir: {}, hospitalId: {}", page, size, sortBy, sortDir, hospitalId);
        ReceptionistPagedResponseDto response = userService.getReceptionists(page, size, sortBy, sortDir, hospitalId);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessages.RECEPTIONIST_LIST_RETRIEVED, response));
    }

    /**
     * Get a single receptionist by ID.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReceptionistResponseDto>> getReceptionist(@PathVariable Long id) {
        log.info("GET /api/v1/receptionists/{} - Fetching receptionist", id);
        try {
            ReceptionistResponseDto dto = userService.getReceptionistById(id);
            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.RECEPTIONIST_RETRIEVED, dto));
        } catch (ResourceNotFoundException rnfe) {
            log.warn("Receptionist {} not found: {}", id, rnfe.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error fetching receptionist {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch receptionist"));
        }
    }

    /**
     * Soft delete a receptionist (sets isActive=false).
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReceptionist(@PathVariable Long id) {
        log.info("DELETE /api/v1/receptionists/{} - Soft deleting receptionist", id);
        try {
            userService.deleteReceptionist(id);
            return ResponseEntity.ok(ApiResponse.success(SuccessMessages.RECEPTIONIST_DELETED, null));
        } catch (ResourceNotFoundException rnfe) {
            log.warn("Receptionist {} not found for deletion: {}", id, rnfe.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(rnfe.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deleting receptionist {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete receptionist"));
        }
    }
}
