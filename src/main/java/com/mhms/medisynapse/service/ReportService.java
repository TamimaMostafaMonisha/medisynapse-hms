package com.mhms.medisynapse.service;

import com.mhms.medisynapse.entity.ReportMetadata;

import java.util.List;

public interface ReportService {

    /**
     * Generate a billing invoice report
     *
     * @param billingId The billing ID
     * @param createdBy The user creating the report
     * @return ReportMetadata containing file path and metadata
     */
    ReportMetadata generateInvoiceReport(Long billingId, Long createdBy);

    /**
     * Generate an insurance claim report
     *
     * @param claimId   The claim ID
     * @param createdBy The user creating the report
     * @return ReportMetadata containing file path and metadata
     */
    ReportMetadata generateClaimReport(Long claimId, Long createdBy);

    /**
     * Generate an insurance settlement report
     *
     * @param claimId   The claim ID
     * @param createdBy The user creating the report
     * @return ReportMetadata containing file path and metadata
     */
    ReportMetadata generateSettlementReport(Long claimId, Long createdBy);

    /**
     * Generate a payment receipt report (on-demand, no metadata stored)
     * @param paymentId The payment ID
     * @return byte array of the PDF report
     */
    byte[] generateReceiptReport(Long paymentId);

    /**
     * Get all reports for a specific billing
     *
     * @param billingId The billing ID
     * @return List of report metadata
     */
    List<ReportMetadata> getReportsByBillingId(Long billingId);

    /**
     * Get all reports for a specific claim
     *
     * @param claimId The claim ID
     * @return List of report metadata
     */
    List<ReportMetadata> getReportsByClaimId(Long claimId);

    /**
     * Get report by ID
     *
     * @param reportId The report ID
     * @return ReportMetadata
     */
    ReportMetadata getReportById(Long reportId);
}

