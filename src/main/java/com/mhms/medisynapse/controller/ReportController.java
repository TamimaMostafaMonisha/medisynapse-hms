package com.mhms.medisynapse.controller;

import com.mhms.medisynapse.dto.ApiResponse;
import com.mhms.medisynapse.entity.ReportMetadata;
import com.mhms.medisynapse.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @PostMapping(value = "/invoice/{billingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReportMetadata>> generateInvoice(
            @PathVariable Long billingId,
            @RequestParam Long createdBy) {

        log.info("Generating invoice for billing ID: {}", billingId);

        ReportMetadata report = reportService.generateInvoiceReport(billingId, createdBy);

        log.info("Successfully generated invoice report with ID: {}", report.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoice generated successfully", report));
    }

    @PostMapping(value = "/claim/{claimId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReportMetadata>> generateClaimReport(
            @PathVariable Long claimId,
            @RequestParam Long createdBy) {

        log.info("Generating claim report for claim ID: {}", claimId);

        ReportMetadata report = reportService.generateClaimReport(claimId, createdBy);

        log.info("Successfully generated claim report with ID: {}", report.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Claim report generated successfully", report));
    }

    @PostMapping(value = "/settlement/{claimId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReportMetadata>> generateSettlementReport(
            @PathVariable Long claimId,
            @RequestParam Long createdBy) {

        log.info("Generating settlement report for claim ID: {}", claimId);

        ReportMetadata report = reportService.generateSettlementReport(claimId, createdBy);

        log.info("Successfully generated settlement report with ID: {}", report.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Settlement report generated successfully", report));
    }

    @GetMapping(value = "/{reportId}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> downloadReport(@PathVariable Long reportId) {

        log.info("Downloading report with ID: {}", reportId);

        ReportMetadata report = reportService.getReportById(reportId);

        File file = new File(report.getFilePath());

        if (!file.exists()) {
            log.error("Report file not found: {}", report.getFilePath());
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        log.info("Successfully serving report file: {}", file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .body(resource);
    }

    @GetMapping(value = "/billing/{billingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ReportMetadata>>> getBillingReports(@PathVariable Long billingId) {

        log.info("Fetching reports for billing ID: {}", billingId);

        List<ReportMetadata> reports = reportService.getReportsByBillingId(billingId);

        return ResponseEntity.ok(
                ApiResponse.success("Billing reports retrieved successfully", reports)
        );
    }

    @GetMapping(value = "/claim/{claimId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<ReportMetadata>>> getClaimReports(@PathVariable Long claimId) {

        log.info("Fetching reports for claim ID: {}", claimId);

        List<ReportMetadata> reports = reportService.getReportsByClaimId(claimId);

        return ResponseEntity.ok(
                ApiResponse.success("Claim reports retrieved successfully", reports)
        );
    }

    @GetMapping(value = "/{reportId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReportMetadata>> getReportById(@PathVariable Long reportId) {

        log.info("Fetching report with ID: {}", reportId);

        ReportMetadata report = reportService.getReportById(reportId);

        return ResponseEntity.ok(
                ApiResponse.success("Report retrieved successfully", report)
        );
    }

    @GetMapping(value = "/receipt/{paymentId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadReceiptReport(@PathVariable Long paymentId) {
        log.info("Generating and downloading receipt report for payment ID: {}", paymentId);

        byte[] pdfBytes = reportService.generateReceiptReport(paymentId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=RECEIPT_" + paymentId + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        log.info("Successfully generated receipt report for payment ID: {}", paymentId);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}

