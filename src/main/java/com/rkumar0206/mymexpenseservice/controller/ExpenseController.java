package com.rkumar0206.mymexpenseservice.controller;

import com.rkumar0206.mymexpenseservice.constantsAndEnums.Constants;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.Headers;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.RequestAction;
import com.rkumar0206.mymexpenseservice.exception.ExpenseException;
import com.rkumar0206.mymexpenseservice.models.request.ExpenseRequest;
import com.rkumar0206.mymexpenseservice.models.response.CustomResponse;
import com.rkumar0206.mymexpenseservice.models.response.ExpenseResponse;
import com.rkumar0206.mymexpenseservice.service.ExpenseService;
import com.rkumar0206.mymexpenseservice.utility.MymUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mym/api/expenses")
public class ExpenseController {

    @Value("${pagination.maxPageSizeAllowed}")
    private int maxPageSizeAllowed;

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/new/create")
    public ResponseEntity<CustomResponse<ExpenseResponse>> createExpense(@RequestHeader(Headers.CORRELATION_ID) String correlationId, @RequestBody ExpenseRequest expenseRequest) {

        CustomResponse<ExpenseResponse> response = new CustomResponse<>();

        if (expenseRequest.isValid(RequestAction.ADD)) {

            try {

                ExpenseResponse expenseResponse = expenseService.create(expenseRequest);

                response.setStatus(HttpStatus.CREATED.value());
                response.setMessage(Constants.SUCCESS);
                response.setBody(expenseResponse);

            } catch (Exception ex) {

                setAppropriateResponseStatus(response, ex);
            }

        } else {

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(String.format(Constants.FAILED_, ErrorMessageConstants.REQUEST_BODY_NOT_VALID));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @PutMapping("/update")
    public ResponseEntity<CustomResponse<ExpenseResponse>> updateExpense(@RequestHeader(Headers.CORRELATION_ID) String correlationId, @RequestBody ExpenseRequest expenseRequest) {

        CustomResponse<ExpenseResponse> response = new CustomResponse<>();

        if (expenseRequest.isValid(RequestAction.UPDATE)) {

            try {

                ExpenseResponse expenseResponse = expenseService.update(expenseRequest);

                response.setStatus(HttpStatus.OK.value());
                response.setMessage(Constants.SUCCESS);
                response.setBody(expenseResponse);

            } catch (Exception ex) {

                setAppropriateResponseStatus(response, ex);
            }

        } else {

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(String.format(Constants.FAILED_, ErrorMessageConstants.REQUEST_BODY_NOT_VALID));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @GetMapping("/uid")
    public ResponseEntity<CustomResponse<Page<ExpenseResponse>>> getAllExpenseByUid(Pageable pageable, @RequestHeader(Headers.CORRELATION_ID) String correlationId) {

        CustomResponse<Page<ExpenseResponse>> response = new CustomResponse<>();

        try {

            if (maxPageSizeAllowed == 0) maxPageSizeAllowed = 200;

            if (pageable.getPageSize() > maxPageSizeAllowed)
                throw new ExpenseException(String.format(ErrorMessageConstants.MAX_PAGE_SIZE_ERROR, maxPageSizeAllowed));

            Page<ExpenseResponse> userExpenses = expenseService.getUserExpenses(pageable);

            response.setStatus(HttpStatus.OK.value());
            response.setBody(userExpenses);
            response.setMessage(Constants.SUCCESS);

        } catch (Exception ex) {

            setAppropriateResponseStatus(response, ex);
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @GetMapping("/categoryKey")
    public ResponseEntity<CustomResponse<Page<ExpenseResponse>>> getAllExpenseByCategoryKey(Pageable pageable, @RequestHeader(Headers.CORRELATION_ID) String correlationId, @RequestParam("categoryKey") String categoryKey) {

        CustomResponse<Page<ExpenseResponse>> response = new CustomResponse<>();

        try {

            if (maxPageSizeAllowed == 0) maxPageSizeAllowed = 200;

            if (!MymUtil.isValid(categoryKey))
                throw new ExpenseException(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID);

            if (pageable.getPageSize() > maxPageSizeAllowed)
                throw new ExpenseException(String.format(ErrorMessageConstants.MAX_PAGE_SIZE_ERROR, maxPageSizeAllowed));

            Page<ExpenseResponse> userExpenses = expenseService.getExpenseByCategoryKey(pageable, categoryKey);

            response.setStatus(HttpStatus.OK.value());
            response.setBody(userExpenses);
            response.setMessage(Constants.SUCCESS);

        } catch (Exception ex) {

            setAppropriateResponseStatus(response, ex);
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }


    @GetMapping("/paymentMethodKeys")
    public ResponseEntity<CustomResponse<Page<ExpenseResponse>>> getByPaymentMethodKeys(
            Pageable pageable,
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam List<String> paymentMethodKeys
    ) {
        CustomResponse<Page<ExpenseResponse>> response = new CustomResponse<>();

        try {

            if (maxPageSizeAllowed == 0) maxPageSizeAllowed = 200;

            if (paymentMethodKeys.isEmpty())
                throw new ExpenseException(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID);

            if (pageable.getPageSize() > maxPageSizeAllowed)
                throw new ExpenseException(String.format(ErrorMessageConstants.MAX_PAGE_SIZE_ERROR, maxPageSizeAllowed));

            Page<ExpenseResponse> userExpenses = expenseService.getUserExpenseByPaymentMethodKey(pageable, paymentMethodKeys);

            response.setStatus(HttpStatus.OK.value());
            response.setBody(userExpenses);
            response.setMessage(Constants.SUCCESS);

        } catch (Exception ex) {

            setAppropriateResponseStatus(response, ex);
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));

    }

    @GetMapping("/dateBetween")
    public ResponseEntity<CustomResponse<Page<ExpenseResponse>>> getByDateBetween(
            Pageable pageable,
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam("startDate") Long startDate,
            @RequestParam("endDate") Long endDate
    ) {
        CustomResponse<Page<ExpenseResponse>> response = new CustomResponse<>();

        try {

            if (maxPageSizeAllowed == 0) maxPageSizeAllowed = 200;

            if (startDate == null || endDate == null)
                throw new ExpenseException(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID);

            if (pageable.getPageSize() > maxPageSizeAllowed)
                throw new ExpenseException(String.format(ErrorMessageConstants.MAX_PAGE_SIZE_ERROR, maxPageSizeAllowed));

            Page<ExpenseResponse> userExpenses = expenseService.getExpenseBetweenStartDateAndEndDate(pageable, startDate, endDate);

            response.setStatus(HttpStatus.OK.value());
            response.setBody(userExpenses);
            response.setMessage(Constants.SUCCESS);

        } catch (Exception ex) {

            setAppropriateResponseStatus(response, ex);
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));

    }

    @GetMapping("/key")
    public ResponseEntity<CustomResponse<ExpenseResponse>> getExpenseByKey(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam("key") String key
    ) {

        CustomResponse<ExpenseResponse> response = new CustomResponse<>();

        try {

            if (!MymUtil.isValid(key))
                throw new ExpenseException(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID);

            ExpenseResponse expenseResponse = expenseService.getExpenseByKey(key);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(expenseResponse);

        } catch (Exception ex) {

            setAppropriateResponseStatus(response, ex);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @DeleteMapping("/key")
    public ResponseEntity<CustomResponse<String>> deleteExpenseByExpenseKey(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam("key") String key
    ) {

        CustomResponse<String> response = new CustomResponse<>();

        try {

            if (!MymUtil.isValid(key))
                throw new ExpenseException(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID);

            expenseService.deleteExpense(key);

            response.setStatus(HttpStatus.NO_CONTENT.value());
            response.setMessage(Constants.SUCCESS);

        } catch (Exception ex) {

            setAppropriateResponseStatus(response, ex);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @DeleteMapping("/categoryKey")
    public ResponseEntity<CustomResponse<String>> deleteExpenseByCategoryKey(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam("categoryKey") String categoryKey
    ) {

        CustomResponse<String> response = new CustomResponse<>();

        try {

            if (!MymUtil.isValid(categoryKey))
                throw new ExpenseException(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID);

            expenseService.deleteExpenseByCategoryKey(categoryKey);

            response.setStatus(HttpStatus.NO_CONTENT.value());
            response.setMessage(Constants.SUCCESS);

        } catch (Exception ex) {

            setAppropriateResponseStatus(response, ex);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }


    private void setAppropriateResponseStatus(CustomResponse response, Exception ex) {

        if (ex instanceof ExpenseException) {

            if (ex.getMessage().startsWith("Max page size should be less than or equal to")) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            } else {

                switch (ex.getMessage()) {

                    case ErrorMessageConstants.PERMISSION_DENIED -> response.setStatus(HttpStatus.FORBIDDEN.value());
                    case ErrorMessageConstants.REQUEST_BODY_NOT_VALID, ErrorMessageConstants.REQUEST_PARAM_NOT_VALID ->
                            response.setStatus(HttpStatus.BAD_REQUEST.value());
                    case ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR ->
                            response.setStatus(HttpStatus.NO_CONTENT.value());
                }
            }

        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        response.setMessage(String.format(Constants.FAILED_, ex.getMessage()));
    }

}
