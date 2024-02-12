package com.rkumar0206.mymexpenseservice.utility;

import com.rkumar0206.mymexpenseservice.constantsAndEnums.Constants;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.models.response.CustomResponse;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class MymUtil {

    public static boolean isValid(String str) {

        return str != null && !str.trim().isEmpty();
    }

    public static boolean isNotValid(String str) {

        return !isValid(str);
    }

    public static String createNewKey(String uid) {

        return uid.substring(0, 8) + "_" + UUID.randomUUID();
    }

    public static void setAppropriateResponseStatus(CustomResponse response, Exception ex) {

        if (ex.getMessage().startsWith("Max page size should be less than or equal to")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } else {

            switch (ex.getMessage()) {

                case ErrorMessageConstants.PERMISSION_DENIED, ErrorMessageConstants.USER_NOT_AUTHORIZED_ERROR, ErrorMessageConstants.USER_INFO_NOT_PROVIDED_ERROR, ErrorMessageConstants.USER_INFO_NOT_PROPER ->
                        response.setStatus(HttpStatus.FORBIDDEN.value());

                case ErrorMessageConstants.REQUEST_BODY_NOT_VALID, ErrorMessageConstants.REQUEST_PARAM_NOT_VALID, ErrorMessageConstants.PAYMENT_METHOD_WITH_THIS_NAME_ALREADY_PRESENT ->
                        response.setStatus(HttpStatus.BAD_REQUEST.value());

                case ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR, ErrorMessageConstants.NO_PAYMENT_METHOD_FOUND_ERROR ->
                        response.setStatus(HttpStatus.NO_CONTENT.value());

                default -> response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }

        response.setMessage(String.format(Constants.FAILED_, ex.getMessage()));
    }

    public static String createLog(String correlationId, String message) {

        return String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, message);
    }

}
