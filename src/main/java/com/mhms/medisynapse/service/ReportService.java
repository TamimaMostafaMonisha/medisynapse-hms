package com.mhms.medisynapse.service;

import com.mhms.medisynapse.entity.ReportMetadata;

import java.util.List;

public interface ReportService {

    /**
     * Generate a billing invoice report
     */
    ReportMetadata generateInvoiceReport(Long billingId, Long createdBy);

    /**
     * Generate an insurance claim report
     */
    ReportMetadata generateClaimReport(Long claimId, Long createdBy);

    /**
     * Generate an insurance settlement report
     */
    ReportMetadata generateSettlementReport(Long claimId, Long createdBy);

    /**
     * Generate a payment receipt report (on-demand, no metadata stored)
     */
    byte[] generateReceiptReport(Long paymentId);

    /**
     * Get all reports for a specific billing
     */
    List<ReportMetadata> getReportsByBillingId(Long billingId);

    /**
     * Get all reports for a specific claim
     */
    List<ReportMetadata> getReportsByClaimId(Long claimId);

    /**
     * Get report by ID
     */
    ReportMetadata getReportById(Long reportId);
}

