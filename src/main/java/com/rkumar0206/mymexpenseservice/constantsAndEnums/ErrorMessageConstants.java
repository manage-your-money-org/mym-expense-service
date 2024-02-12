package com.rkumar0206.mymexpenseservice.constantsAndEnums;

public class ErrorMessageConstants {

    public static final String USER_NOT_AUTHORIZED_ERROR = "User not authorized";
    public static final String USER_INFO_NOT_PROVIDED_ERROR = "User info not provided or error in parsing user info value found in header";
    public static final String PERMISSION_DENIED = "Permission denied";
    public static final String MAX_PAGE_SIZE_ERROR = "Max page size should be less than or equal to %s";
    public static final String REQUEST_BODY_NOT_VALID = "Please send all the mandatory fields in request body";
    public static final String REQUEST_PARAM_NOT_VALID = "Please send valid request parameters";
    public static final String NO_EXPENSE_FOUND_ERROR = "No expense found";
    public static final String NO_PAYMENT_METHOD_FOUND_ERROR = "No payment method found";
    public static final String PAYMENT_METHOD_WITH_THIS_NAME_ALREADY_PRESENT = "Payment method with this name already present";
    public static final String USER_INFO_NOT_PROPER = "User information provided is not proper";
    public static final String NO_CORRELATION_ID_PASSED = "Header " + Headers.CORRELATION_ID + " no passed.";
}
