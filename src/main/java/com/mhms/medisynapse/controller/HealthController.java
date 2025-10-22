package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.constants.ErrorMessages;
import com.mhms.medisynapse.constants.SuccessMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health Check", description = "Health monitoring endpoints for application liveness, readiness, and general health status")
public class HealthController {
    private final DataSource dataSource;

    /**
     * Liveness Probe - Indicates if the application is running
     * This should return 200 if the application is alive
     * Used by Kubernetes to restart the pod if it fails
     */
    @GetMapping("/live")
    @Operation(
            summary = "Liveness Probe",
            description = "Check if the application is alive and running. Used by Kubernetes for pod restart decisions."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Application is alive",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"UP\",\"timestamp\":\"2025-09-24T10:30:00\",\"application\":\"Medisynapse HMS\",\"version\":\"1.0.0\",\"check\":\"liveness\"}"
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> liveness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", SuccessMessages.SYSTEM_HEALTH_CHECK_PASSED);
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Medisynapse HMS");
        response.put("version", "1.0.0");
        response.put("check", "liveness");
        return ResponseEntity.ok(response);
    }

    /**
     * Readiness Probe - Indicates if the application is ready to serve requests
     * This should return 200 only when the application can handle requests
     * Used by Kubernetes to determine if the pod should receive traffic
     */
    @GetMapping("/ready")
    @Operation(
            summary = "Readiness Probe",
            description = "Check if the application is ready to serve traffic. Includes database connectivity check."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Application is ready",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"UP\",\"timestamp\":\"2025-09-24T10:30:00\",\"application\":\"Medisynapse HMS\",\"version\":\"1.0.0\",\"check\":\"readiness\",\"database\":\"UP\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Application is not ready",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"DOWN\",\"timestamp\":\"2025-09-24T10:30:00\",\"application\":\"Medisynapse HMS\",\"version\":\"1.0.0\",\"check\":\"readiness\",\"database\":\"DOWN\",\"error\":\"Database connection failed\"}"
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Medisynapse HMS");
        response.put("version", "1.0.0");
        response.put("check", "readiness");
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                response.put("status", SuccessMessages.SYSTEM_HEALTH_CHECK_PASSED);
                response.put("database", SuccessMessages.DATABASE_CONNECTION_ESTABLISHED);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", ErrorMessages.SERVICE_UNAVAILABLE);
                response.put("database", ErrorMessages.SERVICE_UNAVAILABLE);
                response.put("error", ErrorMessages.SERVICE_UNAVAILABLE);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }
        } catch (SQLException e) {
            response.put("status", ErrorMessages.SERVICE_UNAVAILABLE);
            response.put("database", ErrorMessages.SERVICE_UNAVAILABLE);
            response.put("error", ErrorMessages.SERVICE_UNAVAILABLE + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * General Health Check - Combines liveness and readiness
     */
    @GetMapping("/health")
    @Operation(
            summary = "General Health Check",
            description = "General health status of the application with detailed information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Health check results",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"UP\",\"timestamp\":\"2025-09-24T10:30:00\",\"application\":\"Medisynapse HMS\",\"version\":\"1.0.0\",\"uptime\":\"2 hours 30 minutes\",\"database\":{\"status\":\"UP\",\"responseTime\":\"15ms\"}}"
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Medisynapse HMS");
        response.put("version", "1.0.0");

        // Check database connectivity with timing
        Map<String, Object> databaseHealth = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                long responseTime = System.currentTimeMillis() - startTime;
                databaseHealth.put("status", "UP");
                databaseHealth.put("responseTime", responseTime + "ms");
                response.put("status", "UP");
            } else {
                databaseHealth.put("status", "DOWN");
                databaseHealth.put("error", "Database connection is not valid");
                response.put("status", "DOWN");
            }
        } catch (SQLException e) {
            databaseHealth.put("status", "DOWN");
            databaseHealth.put("error", "Connection failed: " + e.getMessage());
            response.put("status", "DOWN");
        }

        response.put("database", databaseHealth);
        return ResponseEntity.ok(response);
    }
}
