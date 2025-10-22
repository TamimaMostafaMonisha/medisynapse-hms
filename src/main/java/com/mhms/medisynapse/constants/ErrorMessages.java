package com.mhms.medisynapse.constants;

/**
 * Centralized error messages for the entire application.
 * This class contains all error messages used across different modules.
 */
public final class ErrorMessages {

    // ==================== COMMON ERROR MESSAGES ====================
    public static final String INVALID_REQUEST = "Invalid request data provided";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access to this resource";
    public static final String FORBIDDEN_ACCESS = "Access denied to this resource";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later";
    public static final String SERVICE_UNAVAILABLE = "Service is temporarily unavailable";
    public static final String BAD_REQUEST = "Bad request format or invalid parameters";
    public static final String INVALID_JSON_FORMAT = "Invalid JSON format in request body";
    public static final String MISSING_REQUIRED_PARAMETER = "Required parameter '%s' is missing";
    public static final String INVALID_PARAMETER_TYPE = "Invalid value for parameter '%s'. Expected type: %s";
    // ==================== VALIDATION ERROR MESSAGES ====================
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String INVALID_ID_FORMAT = "Invalid ID format: %s";
    public static final String ID_MUST_BE_POSITIVE = "ID must be a positive number";
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format: %s";
    public static final String INVALID_PHONE_FORMAT = "Invalid phone number format: %s";
    public static final String INVALID_DATE_FORMAT = "Invalid date format. Expected format: %s";
    public static final String INVALID_DATE_RANGE = "Start date cannot be after end date";
    public static final String FIELD_CANNOT_BE_NULL = "Field '%s' cannot be null";
    public static final String FIELD_CANNOT_BE_EMPTY = "Field '%s' cannot be empty";
    public static final String FIELD_TOO_LONG = "Field '%s' exceeds maximum length of %d characters";
    public static final String FIELD_TOO_SHORT = "Field '%s' must be at least %d characters long";
    public static final String INVALID_ENUM_VALUE = "Invalid value '%s' for field '%s'. Allowed values: %s";
    public static final String VALUE_OUT_OF_RANGE = "Value for '%s' must be between %d and %d";
    // ==================== HOSPITAL ERROR MESSAGES ====================
    public static final String HOSPITAL_NOT_FOUND = "Hospital not found with ID: %d";
    public static final String HOSPITAL_NOT_FOUND_BY_NAME = "Hospital not found with name: %s";
    public static final String HOSPITAL_ALREADY_EXISTS = "Hospital with name '%s' already exists";
    public static final String HOSPITAL_EMAIL_ALREADY_EXISTS = "Hospital with email '%s' already exists";
    public static final String HOSPITAL_INACTIVE = "Hospital with ID %d is inactive";
    public static final String HOSPITAL_CANNOT_BE_DELETED = "Hospital cannot be deleted: %s";
    public static final String HOSPITAL_HAS_ACTIVE_USERS = "Hospital has %d active users";
    public static final String HOSPITAL_HAS_ACTIVE_PATIENTS = "Hospital has %d active patient associations";
    public static final String HOSPITAL_HAS_ACTIVE_APPOINTMENTS = "Hospital has %d active appointments";
    public static final String AVAILABLE_BEDS_EXCEED_TOTAL = "Available beds (%d) cannot exceed total beds (%d)";
    public static final String INVALID_BED_COUNT = "Bed count must be at least %d";
    public static final String INVALID_STAFF_COUNT = "Staff count must be at least %d";
    public static final String INVALID_DEPARTMENT_COUNT = "Department count must be at least %d";
    // ==================== USER ERROR MESSAGES ====================
    public static final String USER_NOT_FOUND = "User not found with ID: %d";
    public static final String USER_NOT_FOUND_BY_EMAIL = "User not found with email: %s";
    public static final String USER_NOT_FOUND_BY_USERNAME = "User not found with username: %s";
    public static final String USER_ALREADY_EXISTS = "User with email '%s' already exists";
    public static final String USERNAME_ALREADY_EXISTS = "Username '%s' already exists";
    public static final String USER_INACTIVE = "User account is inactive";
    public static final String USER_SUSPENDED = "User account is suspended";
    public static final String USER_LOCKED = "User account is locked";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String PASSWORD_MISMATCH = "Password confirmation does not match";
    public static final String WEAK_PASSWORD = "Password does not meet security requirements";
    public static final String PASSWORD_EXPIRED = "Password has expired";
    public static final String INVALID_ROLE = "Invalid user role: %s";
    public static final String INSUFFICIENT_PERMISSIONS = "Insufficient permissions for this operation";
    // ==================== PATIENT ERROR MESSAGES ====================
    public static final String PATIENT_NOT_FOUND = "Patient not found with ID: %d";
    public static final String PATIENT_NOT_FOUND_BY_NUMBER = "Patient not found with patient number: %s";
    public static final String PATIENT_ALREADY_EXISTS = "Patient already exists with the provided details";
    public static final String PATIENT_INACTIVE = "Patient record is inactive";
    public static final String INVALID_PATIENT_NUMBER = "Invalid patient number format";
    public static final String PATIENT_AGE_INVALID = "Patient age must be between %d and %d years";
    public static final String PATIENT_ALREADY_REGISTERED = "Patient is already registered at this hospital";
    // ==================== APPOINTMENT ERROR MESSAGES ====================
    public static final String APPOINTMENT_NOT_FOUND = "Appointment not found with ID: %d";
    public static final String APPOINTMENT_ALREADY_EXISTS = "Appointment already exists for the specified time slot";
    public static final String APPOINTMENT_CANNOT_BE_CANCELLED = "Appointment cannot be cancelled: %s";
    public static final String APPOINTMENT_CANNOT_BE_MODIFIED = "Appointment cannot be modified: %s";
    public static final String APPOINTMENT_TIME_CONFLICT = "Appointment time conflicts with existing appointment";
    public static final String APPOINTMENT_TIME_PAST = "Cannot schedule appointment in the past";
    public static final String APPOINTMENT_OUTSIDE_WORKING_HOURS = "Appointment time is outside working hours";
    public static final String DOCTOR_NOT_AVAILABLE_ON_CERTAIN_TIME = "Doctor is not available at the requested time";
    public static final String INVALID_APPOINTMENT_STATUS = "Invalid appointment status: %s";
    // ==================== DOCTOR ERROR MESSAGES ====================
    public static final String DOCTOR_NOT_FOUND = "Doctor not found with ID: %d";
    public static final String DOCTOR_NOT_AVAILABLE = "Doctor is not available";
    public static final String DOCTOR_ALREADY_EXISTS = "Doctor with license number '%s' already exists";
    public static final String INVALID_LICENSE_NUMBER = "Invalid medical license number format";
    public static final String LICENSE_EXPIRED = "Medical license has expired";
    public static final String DOCTOR_NOT_IN_DEPARTMENT = "Doctor is not assigned to the specified department";
    // ==================== DEPARTMENT ERROR MESSAGES ====================
    public static final String DEPARTMENT_NOT_FOUND = "Department not found with ID: %d";
    public static final String DEPARTMENT_NOT_FOUND_BY_NAME = "Department not found with name: %s";
    public static final String DEPARTMENT_ALREADY_EXISTS = "Department with name '%s' already exists";
    public static final String DEPARTMENT_HAS_ACTIVE_DOCTORS = "Department has %d active doctors";
    public static final String DEPARTMENT_HAS_ACTIVE_APPOINTMENTS = "Department has %d active appointments";
    public static final String INVALID_DEPARTMENT_TYPE = "Invalid department type: %s";
    // ==================== AUTHENTICATION ERROR MESSAGES ====================
    public static final String AUTHENTICATION_REQUIRED = "Authentication is required";
    public static final String INVALID_TOKEN = "Invalid authentication token";
    public static final String TOKEN_EXPIRED = "Authentication token has expired";
    public static final String TOKEN_MISSING = "Authentication token is missing";
    public static final String SESSION_EXPIRED = "Session has expired. Please login again";
    public static final String ACCOUNT_LOCKED_TOO_MANY_ATTEMPTS = "Account locked due to too many failed login attempts";
    public static final String TWO_FACTOR_REQUIRED = "Two-factor authentication is required";
    public static final String INVALID_2FA_CODE = "Invalid two-factor authentication code";
    // ==================== AUTHORIZATION ERROR MESSAGES ====================
    public static final String ACCESS_DENIED = "Access denied to this resource";
    public static final String ROLE_NOT_AUTHORIZED = "User role '%s' is not authorized for this operation";
    public static final String HOSPITAL_ACCESS_DENIED = "Access denied to hospital resources";
    public static final String PATIENT_ACCESS_DENIED = "Access denied to patient information";
    public static final String ADMIN_ACCESS_REQUIRED = "Administrator access required";
    // ==================== FILE/UPLOAD ERROR MESSAGES ====================
    public static final String FILE_UPLOAD_FAILED = "File upload failed: %s";
    public static final String FILE_NOT_FOUND = "File not found: %s";
    public static final String INVALID_FILE_FORMAT = "Invalid file format. Allowed formats: %s";
    public static final String FILE_SIZE_EXCEEDED = "File size exceeds maximum limit of %s MB";
    public static final String FILE_PROCESSING_ERROR = "Error processing file: %s";
    // ==================== PAGINATION ERROR MESSAGES ====================
    public static final String INVALID_PAGE_NUMBER = "Page number must be non-negative";
    public static final String INVALID_PAGE_SIZE = "Page size must be between %d and %d";
    public static final String INVALID_SORT_FIELD = "Invalid sort field: %s. Valid fields: %s";
    public static final String INVALID_SORT_DIRECTION = "Sort direction must be 'asc' or 'desc'";
    // ==================== DATABASE ERROR MESSAGES ====================
    public static final String DATABASE_CONNECTION_ERROR = "Database connection error";
    public static final String DATA_INTEGRITY_VIOLATION = "Data integrity constraint violation";
    public static final String DUPLICATE_KEY_ERROR = "Duplicate key error: %s";
    public static final String FOREIGN_KEY_VIOLATION = "Foreign key constraint violation";
    public static final String DATABASE_TIMEOUT = "Database operation timeout";
    // ==================== EXTERNAL SERVICE ERROR MESSAGES ====================
    public static final String EXTERNAL_SERVICE_ERROR = "External service error: %s";
    public static final String SERVICE_TIMEOUT = "Service request timeout";
    public static final String SERVICE_UNAVAILABLE_ERROR = "Required service is currently unavailable";
    public static final String API_RATE_LIMIT_EXCEEDED = "API rate limit exceeded. Please try again later";
    // ==================== BUSINESS LOGIC ERROR MESSAGES ====================
    public static final String BUSINESS_RULE_VIOLATION = "Business rule violation: %s";
    public static final String OPERATION_NOT_ALLOWED = "Operation not allowed: %s";
    public static final String INVALID_STATE_TRANSITION = "Invalid state transition from '%s' to '%s'";
    public static final String CONCURRENT_MODIFICATION = "Resource was modified by another user. Please refresh and try again";
    public static final String RESOURCE_LOCKED = "Resource is currently locked by another operation";
    private ErrorMessages() {
        // Private constructor to prevent instantiation
    }
}
