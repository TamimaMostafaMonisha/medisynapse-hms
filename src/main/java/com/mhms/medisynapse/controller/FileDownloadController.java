package com.mhms.medisynapse.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/lab-reports")
@Slf4j
public class FileDownloadController {

    @Value("${file.upload-dir:uploads/lab-reports}")
    private String uploadDir;

    /**
     * GET /lab-reports/{date}/{filename}
     * Download or view lab report file
     */
    @GetMapping("/{date}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String date,
            @PathVariable String filename,
            @RequestParam(defaultValue = "inline") String disposition) {

        try {
            log.info("GET /lab-reports/{}/{} - disposition: {}", date, filename, disposition);

            // Construct file path
            Path filePath = Paths.get(uploadDir, date, filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.error("File not found or not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/pdf"; // Default for lab reports
            }

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            // inline = view in browser, attachment = download
            if ("attachment".equalsIgnoreCase(disposition)) {
                headers.setContentDispositionFormData("attachment", filename);
            } else {
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");
            }

            log.info("Serving file: {} ({})", filename, contentType);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            log.error("Error reading file: {}/{}", date, filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

