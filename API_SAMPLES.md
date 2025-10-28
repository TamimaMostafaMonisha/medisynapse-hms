# Lab Test & Prescription API - Sample Requests & Responses

## 📋 Complete API Documentation with Examples

---

## 1. Create Comprehensive Prescription (Main Workflow Endpoint)

**Endpoint:** `POST /api/v1/doctor/appointments/{appointmentId}/prescriptions/comprehensive`

**Description:** Create prescription with medications and lab test orders in a single request. This is the main endpoint for the new workflow.

**Request:**
```json
{
  "patientId": 456,
  "appointmentId": 123,
  "doctorId": 789,
  "prescriptionType": "PRELIMINARY",
  "medications": [
    {
      "medicationName": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Twice daily",
      "duration": "3 days"
    },
    {
      "medicationName": "Amoxicillin",
      "dosage": "250mg",
      "frequency": "Three times daily",
      "duration": "5 days"
    }
  ],
  "labTestOrders": [
    {
      "patientId": 456,
      "appointmentId": 123,
      "doctorId": 789,
      "testName": "Complete Blood Count (CBC)",
      "testType": "Blood",
      "urgency": "ROUTINE",
      "clinicalNotes": "Patient complaining of fatigue and weakness",
      "suspectedDiagnosis": "Anemia"
    },
    {
      "patientId": 456,
      "appointmentId": 123,
      "doctorId": 789,
      "testName": "Chest X-Ray",
      "testType": "Imaging",
      "urgency": "URGENT",
      "clinicalNotes": "Persistent cough for 2 weeks",
      "suspectedDiagnosis": "Pneumonia"
    },
    {
      "patientId": 456,
      "appointmentId": 123,
      "doctorId": 789,
      "testName": "Liver Function Test (LFT)",
      "testType": "Blood",
      "urgency": "ROUTINE",
      "clinicalNotes": "Elevated liver enzymes suspected",
      "suspectedDiagnosis": "Hepatic dysfunction"
    }
  ],
  "clinicalDiagnosis": "Suspected respiratory infection with anemia",
  "instructions": "Rest for 3 days, avoid cold water, drink plenty of fluids",
  "notes": "Follow up after test results. Patient has history of allergies.",
  "followUpRequired": true,
  "followUpDate": "2025-11-05"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Comprehensive prescription created successfully",
  "data": {
    "prescriptionId": 1001,
    "prescriptionIds": [1001, 1002],
    "labOrderIds": [2001, 2002, 2003],
    "prescriptionType": "PRELIMINARY",
    "medicationCount": 2,
    "labTestCount": 3,
    "message": "Comprehensive prescription created successfully"
  }
}
```

---

## 2. Order Lab Tests Only

**Endpoint:** `POST /api/v1/appointments/{appointmentId}/lab-orders`

**Description:** Order lab tests without prescriptions.

**Request:**
```json
[
  {
    "patientId": 456,
    "appointmentId": 123,
    "doctorId": 789,
    "testName": "Blood Glucose (Fasting)",
    "testType": "Blood",
    "urgency": "STAT",
    "clinicalNotes": "Patient diabetic, emergency check required",
    "suspectedDiagnosis": "Hyperglycemia"
  },
  {
    "patientId": 456,
    "appointmentId": 123,
    "doctorId": 789,
    "testName": "ECG",
    "testType": "Other",
    "urgency": "URGENT",
    "clinicalNotes": "Chest pain reported",
    "suspectedDiagnosis": "Cardiac arrhythmia"
  }
]
```

**Response:**
```json
{
  "success": true,
  "message": "Lab test orders created successfully",
  "data": [
    {
      "id": 2004,
      "patientId": 456,
      "appointmentId": 123,
      "doctorId": 789,
      "prescriptionId": null,
      "testName": "Blood Glucose (Fasting)",
      "testType": "Blood",
      "urgency": "STAT",
      "status": "ORDERED",
      "clinicalNotes": "Patient diabetic, emergency check required",
      "suspectedDiagnosis": "Hyperglycemia",
      "orderedAt": "2025-10-28T10:30:00",
      "sampleCollectedAt": null,
      "completedAt": null,
      "reviewedAt": null,
      "reviewedBy": null,
      "reportFileUrl": null,
      "uploadedBy": null,
      "uploadedAt": null
    },
    {
      "id": 2005,
      "patientId": 456,
      "appointmentId": 123,
      "doctorId": 789,
      "prescriptionId": null,
      "testName": "ECG",
      "testType": "Other",
      "urgency": "URGENT",
      "status": "ORDERED",
      "clinicalNotes": "Chest pain reported",
      "suspectedDiagnosis": "Cardiac arrhythmia",
      "orderedAt": "2025-10-28T10:30:00",
      "sampleCollectedAt": null,
      "completedAt": null,
      "reviewedAt": null,
      "reviewedBy": null,
      "reportFileUrl": null,
      "uploadedBy": null,
      "uploadedAt": null
    }
  ]
}
```

---

## 3. Get Lab Orders for Appointment

**Endpoint:** `GET /api/v1/appointments/{appointmentId}/lab-orders`

**Description:** Retrieve all lab test orders for a specific appointment.

**Request:** `GET /api/v1/appointments/123/lab-orders`

**Response:**
```json
{
  "success": true,
  "message": "Lab orders retrieved successfully",
  "data": [
    {
      "id": 2001,
      "patientId": 456,
      "appointmentId": 123,
      "doctorId": 789,
      "prescriptionId": 1001,
      "testName": "Complete Blood Count (CBC)",
      "testType": "Blood",
      "urgency": "ROUTINE",
      "status": "COMPLETED",
      "clinicalNotes": "Patient complaining of fatigue and weakness",
      "suspectedDiagnosis": "Anemia",
      "orderedAt": "2025-10-28T09:00:00",
      "sampleCollectedAt": "2025-10-28T09:30:00",
      "completedAt": "2025-10-28T14:00:00",
      "reviewedAt": null,
      "reviewedBy": null,
      "reportFileUrl": "https://hospital.com/reports/lab-2001.pdf",
      "uploadedBy": 111,
      "uploadedAt": "2025-10-28T14:05:00"
    },
    {
      "id": 2002,
      "patientId": 456,
      "appointmentId": 123,
      "doctorId": 789,
      "prescriptionId": 1001,
      "testName": "Chest X-Ray",
      "testType": "Imaging",
      "urgency": "URGENT",
      "status": "IN_PROGRESS",
      "clinicalNotes": "Persistent cough for 2 weeks",
      "suspectedDiagnosis": "Pneumonia",
      "orderedAt": "2025-10-28T09:00:00",
      "sampleCollectedAt": null,
      "completedAt": null,
      "reviewedAt": null,
      "reviewedBy": null,
      "reportFileUrl": null,
      "uploadedBy": null,
      "uploadedAt": null
    }
  ]
}
```

---

## 4. Get Pending Lab Results (Doctor)

**Endpoint:** `GET /api/v1/doctors/{doctorId}/lab-results/pending-review`

**Description:** Get lab test results that are completed but awaiting doctor's review.

**Request:** `GET /api/v1/doctors/789/lab-results/pending-review`

**Response:**
```json
{
  "success": true,
  "message": "Pending lab results retrieved successfully",
  "data": [
    {
      "id": 2001,
      "patientId": 456,
      "appointmentId": 123,
      "doctorId": 789,
      "prescriptionId": 1001,
      "testName": "Complete Blood Count (CBC)",
      "testType": "Blood",
      "urgency": "ROUTINE",
      "status": "COMPLETED",
      "clinicalNotes": "Patient complaining of fatigue and weakness",
      "suspectedDiagnosis": "Anemia",
      "orderedAt": "2025-10-28T09:00:00",
      "sampleCollectedAt": "2025-10-28T09:30:00",
      "completedAt": "2025-10-28T14:00:00",
      "reviewedAt": null,
      "reviewedBy": null,
      "reportFileUrl": "https://hospital.com/reports/lab-2001.pdf",
      "uploadedBy": 111,
      "uploadedAt": "2025-10-28T14:05:00"
    },
    {
      "id": 1998,
      "patientId": 450,
      "appointmentId": 120,
      "doctorId": 789,
      "prescriptionId": 995,
      "testName": "Lipid Profile",
      "testType": "Blood",
      "urgency": "ROUTINE",
      "status": "COMPLETED",
      "clinicalNotes": "High cholesterol suspected",
      "suspectedDiagnosis": "Hyperlipidemia",
      "orderedAt": "2025-10-27T11:00:00",
      "sampleCollectedAt": "2025-10-27T11:20:00",
      "completedAt": "2025-10-27T16:00:00",
      "reviewedAt": null,
      "reviewedBy": null,
      "reportFileUrl": "https://hospital.com/reports/lab-1998.pdf",
      "uploadedBy": 111,
      "uploadedAt": "2025-10-27T16:10:00"
    }
  ]
}
```

---

## 5. Update Lab Test Status (Hospital Admin)

**Endpoint:** `PUT /api/v1/lab-orders/{labOrderId}/status`

**Description:** Update the status of a lab test order (used by hospital admin/lab technician).

### Example 1: Sample Collected
**Request:** `PUT /api/v1/lab-orders/2001/status`
```json
{
  "status": "SAMPLE_COLLECTED"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Lab test status updated successfully",
  "data": {
    "id": 2001,
    "patientId": 456,
    "appointmentId": 123,
    "doctorId": 789,
    "prescriptionId": 1001,
    "testName": "Complete Blood Count (CBC)",
    "testType": "Blood",
    "urgency": "ROUTINE",
    "status": "SAMPLE_COLLECTED",
    "clinicalNotes": "Patient complaining of fatigue and weakness",
    "suspectedDiagnosis": "Anemia",
    "orderedAt": "2025-10-28T09:00:00",
    "sampleCollectedAt": "2025-10-28T09:30:00",
    "completedAt": null,
    "reviewedAt": null,
    "reviewedBy": null,
    "reportFileUrl": null,
    "uploadedBy": null,
    "uploadedAt": null
  }
}
```

### Example 2: Test Completed with Report
**Request:** `PUT /api/v1/lab-orders/2001/status`
```json
{
  "status": "COMPLETED",
  "reportFileUrl": "https://hospital.com/reports/lab-2001.pdf"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Lab test status updated successfully",
  "data": {
    "id": 2001,
    "patientId": 456,
    "appointmentId": 123,
    "doctorId": 789,
    "prescriptionId": 1001,
    "testName": "Complete Blood Count (CBC)",
    "testType": "Blood",
    "urgency": "ROUTINE",
    "status": "COMPLETED",
    "clinicalNotes": "Patient complaining of fatigue and weakness",
    "suspectedDiagnosis": "Anemia",
    "orderedAt": "2025-10-28T09:00:00",
    "sampleCollectedAt": "2025-10-28T09:30:00",
    "completedAt": "2025-10-28T14:00:00",
    "reviewedAt": null,
    "reviewedBy": null,
    "reportFileUrl": "https://hospital.com/reports/lab-2001.pdf",
    "uploadedBy": 111,
    "uploadedAt": "2025-10-28T14:05:00"
  }
}
```

---

## 6. Review Lab Result (Doctor)

**Endpoint:** `PUT /api/v1/lab-results/{labResultId}/review?doctorId={doctorId}`

**Description:** Mark a lab result as reviewed by the doctor.

**Request:** `PUT /api/v1/lab-results/2001/review?doctorId=789`
```json
{
  "reviewNotes": "CBC results reviewed. Hemoglobin 10.5 g/dL indicates mild anemia. Iron supplementation recommended."
}
```

**Response:**
```json
{
  "success": true,
  "message": "Lab result marked as reviewed successfully",
  "data": {
    "id": 2001,
    "patientId": 456,
    "appointmentId": 123,
    "doctorId": 789,
    "prescriptionId": 1001,
    "testName": "Complete Blood Count (CBC)",
    "testType": "Blood",
    "urgency": "ROUTINE",
    "status": "REVIEWED",
    "clinicalNotes": "Patient complaining of fatigue and weakness",
    "suspectedDiagnosis": "Anemia",
    "orderedAt": "2025-10-28T09:00:00",
    "sampleCollectedAt": "2025-10-28T09:30:00",
    "completedAt": "2025-10-28T14:00:00",
    "reviewedAt": "2025-10-28T15:30:00",
    "reviewedBy": 789,
    "reportFileUrl": "https://hospital.com/reports/lab-2001.pdf",
    "uploadedBy": 111,
    "uploadedAt": "2025-10-28T14:05:00"
  }
}
```

---

## 7. Get Hospital Lab Orders (Hospital Admin)

**Endpoint:** `GET /api/v1/hospitals/{hospitalId}/lab-orders?statuses=ORDERED,IN_PROGRESS`

**Description:** Get all lab orders for a hospital filtered by status.

**Request:** `GET /api/v1/hospitals/1/lab-orders?statuses=ORDERED,SAMPLE_COLLECTED,IN_PROGRESS`

**Response:**
```json
{
  "success": true,
  "message": "Lab orders retrieved successfully",
  "data": [
    {
      "id": 2005,
      "patientId": 460,
      "appointmentId": 125,
      "doctorId": 790,
      "prescriptionId": null,
      "testName": "Thyroid Function Test",
      "testType": "Blood",
      "urgency": "ROUTINE",
      "status": "ORDERED",
      "clinicalNotes": "Weight gain and fatigue",
      "suspectedDiagnosis": "Hypothyroidism",
      "orderedAt": "2025-10-28T11:00:00",
      "sampleCollectedAt": null,
      "completedAt": null,
      "reviewedAt": null,
      "reviewedBy": null,
      "reportFileUrl": null,
      "uploadedBy": null,
      "uploadedAt": null
    },
    {
      "id": 2002,
      "patientId": 456,
      "appointmentId": 123,
      "doctorId": 789,
      "prescriptionId": 1001,
      "testName": "Chest X-Ray",
      "testType": "Imaging",
      "urgency": "URGENT",
      "status": "IN_PROGRESS",
      "clinicalNotes": "Persistent cough for 2 weeks",
      "suspectedDiagnosis": "Pneumonia",
      "orderedAt": "2025-10-28T09:00:00",
      "sampleCollectedAt": "2025-10-28T10:00:00",
      "completedAt": null,
      "reviewedAt": null,
      "reviewedBy": null,
      "reportFileUrl": null,
      "uploadedBy": null,
      "uploadedAt": null
    }
  ]
}
```

---

## 8. Get Prescription History

**Endpoint:** `GET /api/v1/doctor/appointments/{appointmentId}/prescriptions/history`

**Description:** Get prescription history for an appointment including lab test counts.

**Request:** `GET /api/v1/doctor/appointments/123/prescriptions/history`

**Response:**
```json
{
  "success": true,
  "message": "Prescription history retrieved successfully",
  "data": [
    {
      "id": 1005,
      "appointmentId": 123,
      "prescriptionType": "FINAL",
      "createdAt": "2025-11-05T14:00:00",
      "doctorName": "Dr. John Smith",
      "medicationCount": 3,
      "labTestOrderCount": 0,
      "status": "ACTIVE"
    },
    {
      "id": 1001,
      "appointmentId": 123,
      "prescriptionType": "PRELIMINARY",
      "createdAt": "2025-10-28T09:00:00",
      "doctorName": "Dr. John Smith",
      "medicationCount": 2,
      "labTestOrderCount": 3,
      "status": "SUPERSEDED"
    }
  ]
}
```

---

## Test Type Reference

Valid values for `testType`:
- `Blood` - Blood tests (CBC, LFT, KFT, etc.)
- `Urine` - Urine tests
- `Imaging` - X-Ray, CT Scan, MRI, Ultrasound
- `Biopsy` - Tissue biopsy
- `Other` - ECG, EEG, etc.

## Urgency Levels

Valid values for `urgency`:
- `ROUTINE` - Normal priority
- `URGENT` - High priority
- `STAT` - Immediate (emergency)

## Lab Test Status Flow

```
ORDERED 
  ↓
SAMPLE_COLLECTED 
  ↓
IN_PROGRESS 
  ↓
COMPLETED 
  ↓
REVIEWED
```

Or:
```
ORDERED → CANCELLED
```

## Prescription Types

- `PRELIMINARY` - Given before lab tests, may be superseded
- `FINAL` - Final prescription after reviewing lab results

---

## Error Responses

### 404 Not Found
```json
{
  "success": false,
  "message": "Appointment not found with id: 123",
  "data": null,
  "error": "NOT_FOUND"
}
```

### 400 Bad Request
```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "error": {
    "testName": "Test name is required",
    "testType": "Test type is required"
  }
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "Access denied",
  "data": null,
  "error": "FORBIDDEN"
}
```

---

## Postman Collection

Import this JSON into Postman for easy testing:

[Download: medisynapse-lab-tests.postman_collection.json]

---

**Last Updated:** October 28, 2025  
**API Version:** v1

