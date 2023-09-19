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

    Optional<Expense> getExpenseByKey(String key);

    Page<Expense> getUserExpenses(Pageable pageable);

    Page<Expense> getUserExpenseByPaymentMethodKey(Pageable pageable, List<String> paymentMethodKeys);

    Page<Expense> getExpenseBetweenStartDateAndEndDate(Pageable pageable, Long startDate, Long endDate);

    Page<Expense> getExpenseByCategoryKey(Pageable pageable, String categoryKey);

    List<PaymentMethod> getAllPaymentMethodsOfUser();

    List<PaymentMethod> getPaymentMethodsByKeys(List<String> keys);
}
