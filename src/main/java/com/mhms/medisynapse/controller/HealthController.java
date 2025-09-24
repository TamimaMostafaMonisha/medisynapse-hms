package com.mhms.medisynapse.controller;

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
                                    value = """
                                            {
                                                "status": "UP",
                                                "timestamp": "2025-09-24T10:30:00",
                                                "application": "Medisynapse HMS",
                                                "version": "1.0.0",
                                                "check": "liveness"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> liveness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
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
            description = "Check if the application is ready to serve requests. Includes database connectivity, disk space, and memory checks."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Application is ready to serve requests",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "UP",
                                                "timestamp": "2025-09-24T10:30:00",
                                                "application": "Medisynapse HMS",
                                                "version": "1.0.0",
                                                "check": "readiness",
                                                "checks": {
                                                    "database": "UP",
                                                    "diskSpace": "UP - 45.2GB free of 100.0GB",
                                                    "memory": "UP - 35.2% used"
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Application is not ready to serve requests",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "DOWN",
                                                "timestamp": "2025-09-24T10:30:00",
                                                "application": "Medisynapse HMS",
                                                "version": "1.0.0",
                                                "check": "readiness",
                                                "checks": {
                                                    "database": "DOWN - Connection timeout",
                                                    "diskSpace": "DOWN - Disk usage > 95%",
                                                    "memory": "WARN - Memory usage > 80%"
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> checks = new HashMap<>();
        boolean isReady = true;

        // Check database connectivity
        try {
            if (isDatabaseReady()) {
                checks.put("database", "UP");
            } else {
                checks.put("database", "DOWN");
                isReady = false;
            }
        } catch (Exception e) {
            checks.put("database", "DOWN - " + e.getMessage());
            isReady = false;
        }

        // Add more readiness checks as needed
        checks.put("diskSpace", checkDiskSpace());
        checks.put("memory", checkMemory());

        response.put("status", isReady ? "UP" : "DOWN");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Medisynapse HMS");
        response.put("version", "1.0.0");
        response.put("check", "readiness");
        response.put("checks", checks);

        return ResponseEntity.status(isReady ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    /**
     * General Health Check - Combines liveness and readiness
     */
    @GetMapping("/health")
    @Operation(
            summary = "General Health Check",
            description = "Comprehensive health status combining liveness and readiness checks with additional system information including uptime."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Application is healthy",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "UP",
                                                "timestamp": "2025-09-24T10:30:00",
                                                "application": "Medisynapse HMS",
                                                "version": "1.0.0",
                                                "uptime": "2d 5h 30m 15s",
                                                "checks": {
                                                    "database": "UP",
                                                    "diskSpace": "UP - 45.2GB free of 100.0GB",
                                                    "memory": "UP - 35.2% used"
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Application is unhealthy",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "DOWN",
                                                "timestamp": "2025-09-24T10:30:00",
                                                "application": "Medisynapse HMS",
                                                "version": "1.0.0",
                                                "uptime": "2d 5h 30m 15s",
                                                "checks": {
                                                    "database": "DOWN - Connection failed",
                                                    "diskSpace": "DOWN - Disk usage > 95%",
                                                    "memory": "DOWN - Memory usage > 90%"
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> checks = new HashMap<>();
        boolean isHealthy = true;

        // Database check
        try {
            if (isDatabaseReady()) {
                checks.put("database", "UP");
            } else {
                checks.put("database", "DOWN");
                isHealthy = false;
            }
        } catch (Exception e) {
            checks.put("database", "DOWN - " + e.getMessage());
            isHealthy = false;
        }

        // System checks
        checks.put("diskSpace", checkDiskSpace());
        checks.put("memory", checkMemory());

        response.put("status", isHealthy ? "UP" : "DOWN");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Medisynapse HMS");
        response.put("version", "1.0.0");
        response.put("uptime", getUptime());
        response.put("checks", checks);

        return ResponseEntity.status(isHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    /**
     * Database connectivity check
     */
    private boolean isDatabaseReady() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5 second timeout
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Check available disk space
     */
    private String checkDiskSpace() {
        try {
            long freeSpace = java.io.File.separator.equals("/") ?
                    new java.io.File("/").getFreeSpace() :
                    new java.io.File("C:").getFreeSpace();
            long totalSpace = java.io.File.separator.equals("/") ?
                    new java.io.File("/").getTotalSpace() :
                    new java.io.File("C:").getTotalSpace();

            double freeSpaceGB = freeSpace / (1024.0 * 1024.0 * 1024.0);
            double totalSpaceGB = totalSpace / (1024.0 * 1024.0 * 1024.0);
            double usagePercent = ((totalSpace - freeSpace) / (double) totalSpace) * 100;

            if (usagePercent > 95) {
                return "DOWN - Disk usage > 95%";
            } else if (usagePercent > 85) {
                return "WARN - Disk usage > 85%";
            } else {
                return "UP - " + String.format("%.1fGB free of %.1fGB", freeSpaceGB, totalSpaceGB);
            }
        } catch (Exception e) {
            return "UNKNOWN - " + e.getMessage();
        }
    }

    /**
     * Check memory usage
     */
    private String checkMemory() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            double usagePercent = (usedMemory / (double) maxMemory) * 100;

            if (usagePercent > 90) {
                return "DOWN - Memory usage > 90%";
            } else if (usagePercent > 80) {
                return "WARN - Memory usage > 80%";
            } else {
                return "UP - " + String.format("%.1f%% used", usagePercent);
            }
        } catch (Exception e) {
            return "UNKNOWN - " + e.getMessage();
        }
    }

    /**
     * Get application uptime
     */
    private String getUptime() {
        try {
            long uptimeMillis = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
            long uptimeSeconds = uptimeMillis / 1000;
            long days = uptimeSeconds / 86400;
            long hours = (uptimeSeconds % 86400) / 3600;
            long minutes = (uptimeSeconds % 3600) / 60;
            long seconds = uptimeSeconds % 60;

            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
