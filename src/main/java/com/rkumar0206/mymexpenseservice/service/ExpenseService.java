package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.models.request.ExpenseRequest;
import com.rkumar0206.mymexpenseservice.models.response.ExpenseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {

    ExpenseResponse create(ExpenseRequest request);

    ExpenseResponse update(ExpenseRequest request);

    ExpenseResponse getExpenseByKey(String key);

    Page<ExpenseResponse> getUserExpenses(Pageable pageable);

    Page<ExpenseResponse> getUserExpenseByPaymentMethodKey(Pageable pageable, List<String> paymentMethodKeys);

    Page<ExpenseResponse> getExpenseBetweenStartDateAndEndDate(Pageable pageable, Long startDate, Long endDate);

    Page<ExpenseResponse> getExpenseByCategoryKey(Pageable pageable, String categoryKey);

    void deleteExpense(String key);

    void deleteExpenseByCategoryKey(String categoryKey);

}
