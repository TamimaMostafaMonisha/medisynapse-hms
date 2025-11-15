package com.mhms.medisynapse.service.impl;

import com.mhms.medisynapse.entity.BillItem;
import com.mhms.medisynapse.entity.Billing;
import com.mhms.medisynapse.entity.InsuranceClaim;
import com.mhms.medisynapse.entity.InsuranceSettlement;
import com.mhms.medisynapse.entity.Payment;
import com.mhms.medisynapse.entity.Patient;
import com.mhms.medisynapse.entity.Hospital;
import com.mhms.medisynapse.entity.ReportMetadata;
import com.mhms.medisynapse.repository.BillItemRepository;
import com.mhms.medisynapse.repository.BillingRepository;
import com.mhms.medisynapse.repository.InsuranceClaimRepository;
import com.mhms.medisynapse.repository.InsuranceSettlementRepository;
import com.mhms.medisynapse.repository.PaymentRepository;
import com.mhms.medisynapse.repository.ReportMetadataRepository;
import com.mhms.medisynapse.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportMetadataRepository reportMetadataRepository;
    private final BillingRepository billingRepository;
    private final InsuranceClaimRepository insuranceClaimRepository;
    private final BillItemRepository billItemRepository;
    private final InsuranceSettlementRepository insuranceSettlementRepository;
    private final PaymentRepository paymentRepository;

    @Value("${app.reports.storage-path:uploads/reports}")
    private String reportsStoragePath;


    @Override
    @Transactional
    public ReportMetadata generateInvoiceReport(Long billingId, Long createdBy) {
        log.info("Generating invoice report for billing ID: {}", billingId);

        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new RuntimeException("Billing not found with ID: " + billingId));

        List<BillItem> billItems = billItemRepository.findByBillingId(billingId);

        try {
            // Prepare parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("billNumber", billing.getBillNumber());
            parameters.put("billDate", billing.getBillDate() != null ?
                    billing.getBillDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : "");
            parameters.put("patientName", billing.getPatient().getFirstName() + " " + billing.getPatient().getLastName());
            parameters.put("patientId", billing.getPatient().getId().toString());
            parameters.put("hospitalName", billing.getHospital() != null ?
                    billing.getHospital().getName() : "Hospital Name");
            parameters.put("totalAmount", billing.getTotalAmount());
            parameters.put("discountAmount", billing.getDiscountAmount());
            parameters.put("taxAmount", billing.getTaxAmount());
            parameters.put("netAmount", billing.getNetAmount());
            parameters.put("paidAmount", billing.getPaidAmount());
            parameters.put("outstandingAmount", billing.getOutstandingAmount());
            parameters.put("status", billing.getStatus().toString());
            parameters.put("notes", billing.getNotes());

            // Prepare data source
            List<Map<String, Object>> itemDataList = billItems.stream()
                    .map(item -> {
                        Map<String, Object> itemData = new HashMap<>();
                        itemData.put("serviceType", item.getServiceType());
                        itemData.put("description", item.getDescription());
                        itemData.put("quantity", item.getQuantity());
                        itemData.put("unitPrice", item.getUnitPrice());
                        itemData.put("total", item.getTotal());
                        return itemData;
                    })
                    .collect(Collectors.toList());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itemDataList);

            // Generate report
            String fileName = generateFileName("INVOICE", billingId);
            String filePath = generateJasperReport("invoice_report.jrxml", parameters, dataSource, fileName);

            // Save metadata
            ReportMetadata metadata = new ReportMetadata();
            metadata.setBilling(billing);
            metadata.setReportType("INVOICE");
            metadata.setFilePath(filePath);
            metadata.setCreatedBy(createdBy);

            metadata = reportMetadataRepository.save(metadata);
            log.info("Invoice report generated successfully with ID: {}", metadata.getId());

            return metadata;

        } catch (Exception e) {
            log.error("Error generating invoice report", e);
            throw new RuntimeException("Failed to generate invoice report: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public ReportMetadata generateClaimReport(Long claimId, Long createdBy) {
        log.info("Generating claim report for claim ID: {}", claimId);

        InsuranceClaim claim = insuranceClaimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Insurance claim not found with ID: " + claimId));

        try {
            // Prepare parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("claimId", claim.getId().toString());
            parameters.put("billNumber", claim.getBilling().getBillNumber());
            parameters.put("patientName", claim.getBilling().getPatient().getFirstName() + " " +
                    claim.getBilling().getPatient().getLastName());
            parameters.put("policyNumber", claim.getPolicy().getPolicyNumber());
            parameters.put("provider", claim.getPolicy().getProvider());
            parameters.put("claimAmount", claim.getClaimAmount());
            parameters.put("status", claim.getStatus().toString());
            parameters.put("submittedDate", claim.getSubmittedDt() != null ?
                    claim.getSubmittedDt().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")) : "");
            parameters.put("settledDate", claim.getSettledDt() != null ?
                    claim.getSettledDt().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")) : null);
            parameters.put("hospitalName", claim.getBilling().getHospital() != null ?
                    claim.getBilling().getHospital().getName() : "Hospital Name");

            // Generate report (no detail data needed for claim report)
            String fileName = generateFileName("CLAIM", claimId);
            String filePath = generateJasperReport("claim_report.jrxml", parameters,
                    new JREmptyDataSource(), fileName);

            // Save metadata
            ReportMetadata metadata = new ReportMetadata();
            metadata.setClaim(claim);
            metadata.setReportType("CLAIM_REPORT");
            metadata.setFilePath(filePath);
            metadata.setCreatedBy(createdBy);

            metadata = reportMetadataRepository.save(metadata);
            log.info("Claim report generated successfully with ID: {}", metadata.getId());

            return metadata;

        } catch (Exception e) {
            log.error("Error generating claim report", e);
            throw new RuntimeException("Failed to generate claim report: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public ReportMetadata generateSettlementReport(Long claimId, Long createdBy) {
        log.info("Generating settlement report for claim ID: {}", claimId);

        InsuranceClaim claim = insuranceClaimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Insurance claim not found with ID: " + claimId));

        // Validate claim is settled
        if (claim.getStatus() != InsuranceClaim.ClaimStatus.SETTLED) {
            throw new RuntimeException("Cannot generate settlement report for non-settled claim");
        }

        List<InsuranceSettlement> settlements = insuranceSettlementRepository.findByClaimId(claimId);
        if (settlements.isEmpty()) {
            throw new RuntimeException("No settlement found for claim ID: " + claimId);
        }

        InsuranceSettlement settlement = settlements.get(0); // Get the first settlement

        try {
            // Prepare parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("settlementId", settlement.getId().toString());
            parameters.put("claimId", claim.getId().toString());
            parameters.put("billNumber", claim.getBilling().getBillNumber());
            parameters.put("patientName", claim.getBilling().getPatient().getFirstName() + " " +
                    claim.getBilling().getPatient().getLastName());
            parameters.put("policyNumber", claim.getPolicy().getPolicyNumber());
            parameters.put("provider", claim.getPolicy().getProvider());
            parameters.put("claimAmount", claim.getClaimAmount());
            parameters.put("amountSettled", settlement.getAmountSettled());
            parameters.put("settlementDate", settlement.getSettlementDate() != null ?
                    settlement.getSettlementDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")) : "");
            parameters.put("remarks", settlement.getRemarks());
            parameters.put("hospitalName", claim.getBilling().getHospital() != null ?
                    claim.getBilling().getHospital().getName() : "Hospital Name");

            // Generate report
            String fileName = generateFileName("SETTLEMENT", claimId);
            String filePath = generateJasperReport("settlement_report.jrxml", parameters,
                    new JREmptyDataSource(), fileName);

            // Save metadata
            ReportMetadata metadata = new ReportMetadata();
            metadata.setClaim(claim);
            metadata.setReportType("SETTLEMENT_REPORT");
            metadata.setFilePath(filePath);
            metadata.setCreatedBy(createdBy);

            metadata = reportMetadataRepository.save(metadata);
            log.info("Settlement report generated successfully with ID: {}", metadata.getId());

            return metadata;

        } catch (Exception e) {
            log.error("Error generating settlement report", e);
            throw new RuntimeException("Failed to generate settlement report: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generateReceiptReport(Long paymentId) {
        log.info("Generating receipt report for payment ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        Billing billing = payment.getBilling();
        Hospital hospital = billing.getHospital();
        Patient patient = payment.getPatient();
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("hospitalName", hospital.getName());
            parameters.put("hospitalAddress", hospital.getAddressString() != null ? hospital.getAddressString() : (hospital.getAddress() != null ? hospital.getAddress().getFullAddress() : ""));
            parameters.put("hospitalContact", hospital.getPhone() != null ? hospital.getPhone() : "");
            parameters.put("receiptNumber", "RCPT-" + payment.getId());
            parameters.put("patientName", patient.getFirstName() + " " + patient.getLastName());
            parameters.put("paymentDate", payment.getPaymentDate());
            parameters.put("amount", payment.getAmount());
            parameters.put("paymentMethod", payment.getPaymentMethod().toString());
            parameters.put("referenceNo", payment.getReferenceNo() != null ? payment.getReferenceNo() : "N/A");

            // Generate PDF in memory
            byte[] pdfBytes = generateJasperReportBytes("receipt_report.jrxml", parameters, new JREmptyDataSource());

            log.info("Receipt report generated successfully for payment ID: {}", paymentId);
            return pdfBytes;
        } catch (Exception e) {
            log.error("Error generating receipt report", e);
            throw new RuntimeException("Failed to generate receipt report: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ReportMetadata> getReportsByBillingId(Long billingId) {
        log.info("Fetching reports for billing ID: {}", billingId);
        return reportMetadataRepository.findByBillingId(billingId);
    }

    @Override
    public List<ReportMetadata> getReportsByClaimId(Long claimId) {
        log.info("Fetching reports for claim ID: {}", claimId);
        return reportMetadataRepository.findByClaimId(claimId);
    }

    @Override
    public ReportMetadata getReportById(Long reportId) {
        log.info("Fetching report with ID: {}", reportId);
        return reportMetadataRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));
    }


    // Helper methods
    private String generateFileName(String reportType, Long entityId) {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("%s_%d_%s.pdf", reportType, entityId, timestamp);
    }

    private String generateJasperReport(String templateName, Map<String, Object> parameters,
                                        JRDataSource dataSource, String fileName) throws Exception {

        // Create directory if it doesn't exist
        Path directoryPath = Paths.get(reportsStoragePath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Load template
        InputStream reportStream = new ClassPathResource("reports/" + templateName).getInputStream();

        // Compile report
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Fill report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export to PDF
        String outputPath = reportsStoragePath + File.separator + fileName;
        JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);

        log.info("Report generated successfully at: {}", outputPath);
        return outputPath;
    }

    private byte[] generateJasperReportBytes(String templateName, Map<String, Object> parameters,
                                             JRDataSource dataSource) throws Exception {
        // Load template
        InputStream reportStream = new ClassPathResource("reports/" + templateName).getInputStream();

        // Compile report
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Fill report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export to PDF bytes
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        log.info("Report generated successfully in memory");
        return pdfBytes;
    }
}

