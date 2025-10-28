# Hospital Admin Lab Test API Endpoints

## ✅ New Controller Created!

A dedicated `HospitalAdminLabTestController` has been created with all the endpoints the frontend needs.

---

## Available Endpoints

### 1. Get Pending Lab Tests
```
GET /api/v1/hospital-admin/lab-tests/pending?hospitalId={hospitalId}
```

**Description:** Get all lab tests that need processing (ORDERED, SAMPLE_COLLECTED, IN_PROGRESS)

**Request:**
```bash
GET http://localhost:8080/api/v1/hospital-admin/lab-tests/pending?hospitalId=1
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Pending lab tests retrieved successfully",
  "data": [
    {
      "id": 1,
      "testName": "Complete Blood Count (CBC)",
      "testType": "BLOOD_TEST",
      "status": "ORDERED",
      "urgency": "ROUTINE",
      "patientId": 35,
      "appointmentId": 94,
      "orderedAt": "2025-10-29T00:10:00",
      "clinicalNotes": "Routine checkup"
    }
  ]
}
```

---

### 2. Get All Lab Tests
```
GET /api/v1/hospital-admin/lab-tests/all?hospitalId={hospitalId}&statuses=ORDERED,COMPLETED
```

**Description:** Get all lab tests for a hospital, optionally filtered by status

**Request:**
```bash
# Get all statuses
GET http://localhost:8080/api/v1/hospital-admin/lab-tests/all?hospitalId=1

# Filter by specific statuses
GET http://localhost:8080/api/v1/hospital-admin/lab-tests/all?hospitalId=1&statuses=COMPLETED,REVIEWED
```

**Response:**
```json
{
  "success": true,
  "message": "Lab tests retrieved successfully",
  "data": [
    {
      "id": 1,
      "testName": "Blood Glucose (Fasting)",
      "testType": "BLOOD_TEST",
      "status": "COMPLETED",
      "reportFileUrl": "https://hospital.com/reports/lab-1.pdf",
      "completedAt": "2025-10-29T10:00:00"
    }
  ]
}
```

---

### 3. Get Completed Lab Tests
```
GET /api/v1/hospital-admin/lab-tests/completed?hospitalId={hospitalId}
```

**Description:** Get lab tests that are completed but not yet reviewed by doctor

**Request:**
```bash
GET http://localhost:8080/api/v1/hospital-admin/lab-tests/completed?hospitalId=1
```

---

### 4. Update Lab Test Status
```
PUT /api/v1/hospital-admin/lab-tests/{labTestId}/status
```

**Description:** Update the status of a lab test (collect sample, mark in progress, complete with report)

**Request Examples:**

#### Mark Sample Collected
```bash
PUT http://localhost:8080/api/v1/hospital-admin/lab-tests/1/status
Content-Type: application/json

{
  "status": "SAMPLE_COLLECTED"
}
```

#### Mark In Progress
```bash
PUT http://localhost:8080/api/v1/hospital-admin/lab-tests/1/status
Content-Type: application/json

{
  "status": "IN_PROGRESS"
}
```

#### Mark Completed with Report
```bash
PUT http://localhost:8080/api/v1/hospital-admin/lab-tests/1/status
Content-Type: application/json

{
  "status": "COMPLETED",
  "reportFileUrl": "https://hospital.com/reports/lab-test-1.pdf"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Lab test status updated successfully",
  "data": {
    "id": 1,
    "status": "COMPLETED",
    "completedAt": "2025-10-29T14:30:00",
    "reportFileUrl": "https://hospital.com/reports/lab-test-1.pdf"
  }
}
```

---

### 5. Upload Lab Test Report (NEW!)
```
POST /api/v1/hospital-admin/lab-tests/{labTestId}/upload
```

**Description:** Upload PDF report file for a lab test. Automatically marks test as COMPLETED.

**Request:**
```bash
POST http://localhost:8080/api/v1/hospital-admin/lab-tests/1/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

Form Data:
  file: [PDF file]
```

**Request using curl:**
```bash
curl -X POST "http://localhost:8080/api/v1/hospital-admin/lab-tests/1/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/report.pdf"
```

**Request using Postman:**
1. Method: POST
2. URL: `http://localhost:8080/api/v1/hospital-admin/lab-tests/1/upload`
3. Headers: `Authorization: Bearer {token}`
4. Body: 
   - Select "form-data"
   - Key: `file`, Type: File
   - Select PDF file

**Response:**
```json
{
  "success": true,
  "message": "Lab test report uploaded successfully",
  "data": {
    "filename": "sterling-accuris-pathology-sample-report_a1b2c3d4-e5f6.pdf",
    "originalFilename": "sterling-accuris-pathology-sample-report.pdf",
    "fileUrl": "/lab-reports/2025-10-29/sterling-accuris-pathology-sample-report_a1b2c3d4-e5f6.pdf",
    "fileSize": 245678,
    "contentType": "application/pdf",
    "uploadedAt": "2025-10-29T00:30:00",
    "labTestId": 1,
    "labTestStatus": "COMPLETED"
  }
}
```

**File Storage:**
- Files are stored in: `uploads/lab-reports/YYYY-MM-DD/`
- Filename format: `originalName_UUID.pdf`
- Example: `uploads/lab-reports/2025-10-29/report_a1b2c3d4-e5f6.pdf`

**Validation:**
- Only PDF files accepted
- Maximum file size: 10MB
- File must not be empty

---

### 6. Get Lab Test Statistics
```
GET /api/v1/hospital-admin/lab-tests/stats?hospitalId={hospitalId}
```

**Description:** Get statistics about lab tests for dashboard

**Request:**
```bash
GET http://localhost:8080/api/v1/hospital-admin/lab-tests/stats?hospitalId=1
```

**Response:**
```json
{
  "success": true,
  "message": "Lab test statistics retrieved successfully",
  "data": {
    "orderedCount": 5,
    "sampleCollectedCount": 3,
    "inProgressCount": 2,
    "completedCount": 8,
    "reviewedCount": 15,
    "totalPending": 10
  }
}
```

---

## Status Flow

### Lab Test Status Workflow
```
ORDERED
  ↓ (Lab technician collects sample)
SAMPLE_COLLECTED
  ↓ (Lab starts processing)
IN_PROGRESS
  ↓ (Lab completes test and uploads report)
COMPLETED
  ↓ (Doctor reviews result)
REVIEWED
```

Or:
```
ORDERED → CANCELLED (if needed)
```

---

## Valid Status Values

For updating lab test status:
- `SAMPLE_COLLECTED` - Sample has been collected from patient
- `IN_PROGRESS` - Test is being processed in lab
- `COMPLETED` - Test is complete, report uploaded
- `CANCELLED` - Test was cancelled

**Note:** Status `REVIEWED` is set by doctor, not hospital admin

---

## Security

All endpoints require:
- **Authentication:** Valid JWT token
- **Authorization:** `ADMIN` or `HOSPITAL_ADMIN` role

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Complete Workflow Example

### Scenario: Hospital Admin Processing Lab Test

#### Step 1: View Pending Tests
```bash
GET /api/v1/hospital-admin/lab-tests/pending?hospitalId=1
```

Response shows test ID 123 is ORDERED.

#### Step 2: Collect Sample
```bash
PUT /api/v1/hospital-admin/lab-tests/123/status
{
  "status": "SAMPLE_COLLECTED"
}
```

#### Step 3: Start Processing
```bash
PUT /api/v1/hospital-admin/lab-tests/123/status
{
  "status": "IN_PROGRESS"
}
```

#### Step 4: Complete and Upload Report
```bash
PUT /api/v1/hospital-admin/lab-tests/123/status
{
  "status": "COMPLETED",
  "reportFileUrl": "https://hospital.com/reports/test-123.pdf"
}
```

#### Step 5: Check Completed Tests
```bash
GET /api/v1/hospital-admin/lab-tests/completed?hospitalId=1
```

Now doctor can review the result!

---

## Frontend Integration

### React/Angular Example

```typescript
// Get pending tests
const getPendingTests = async (hospitalId: number) => {
  const response = await fetch(
    `/api/v1/hospital-admin/lab-tests/pending?hospitalId=${hospitalId}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  return response.json();
};

// Update status
const updateLabTestStatus = async (labTestId: number, status: string, reportUrl?: string) => {
  const response = await fetch(
    `/api/v1/hospital-admin/lab-tests/${labTestId}/status`,
    {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        status: status,
        reportFileUrl: reportUrl
      })
    }
  );
  return response.json();
};

// Get statistics for dashboard
const getLabTestStats = async (hospitalId: number) => {
  const response = await fetch(
    `/api/v1/hospital-admin/lab-tests/stats?hospitalId=${hospitalId}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  return response.json();
};
```

---

## Dashboard Widget Example

```typescript
// Display statistics on dashboard
const DashboardStats = ({ hospitalId }) => {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    getLabTestStats(hospitalId).then(response => {
      setStats(response.data);
    });
  }, [hospitalId]);

  return (
    <div className="stats-grid">
      <StatCard title="Pending" count={stats?.totalPending} color="warning" />
      <StatCard title="Ordered" count={stats?.orderedCount} color="info" />
      <StatCard title="In Progress" count={stats?.inProgressCount} color="primary" />
      <StatCard title="Completed" count={stats?.completedCount} color="success" />
    </div>
  );
};
```

---

## Error Responses

### 404 Not Found
```json
{
  "success": false,
  "message": "Lab test order not found with id: 123",
  "error": "NOT_FOUND"
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "Access denied",
  "error": "FORBIDDEN"
}
```

### 400 Bad Request
```json
{
  "success": false,
  "message": "Validation failed",
  "error": {
    "status": "Status is required"
  }
}
```

---

## Testing with Postman/Curl

### Get Pending Tests
```bash
curl -X GET "http://localhost:8080/api/v1/hospital-admin/lab-tests/pending?hospitalId=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Update Status
```bash
curl -X PUT "http://localhost:8080/api/v1/hospital-admin/lab-tests/1/status" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "COMPLETED",
    "reportFileUrl": "https://hospital.com/reports/test-1.pdf"
  }'
```

---

## Summary

✅ **7 endpoints created** for Hospital Admin lab test management  
✅ **File upload support** for PDF lab reports  
✅ **Automatic status update** when file uploaded  
✅ **UUID-based unique filenames** to prevent conflicts  
✅ **Date-based folder structure** for organization  
✅ **All CRUD operations** supported  
✅ **Statistics endpoint** for dashboard  
✅ **Proper security** with role-based access  
✅ **Complete workflow** support  

**Status:** Ready to use! Restart application and test.

---

**Date:** October 29, 2025  
**Controller:** `HospitalAdminLabTestController.java`  
**Base Path:** `/api/v1/hospital-admin/lab-tests`  
**Security:** `@PreAuthorize("hasAnyRole('ADMIN', 'HOSPITAL_ADMIN')")`
**Upload Directory:** `uploads/lab-reports/YYYY-MM-DD/`

