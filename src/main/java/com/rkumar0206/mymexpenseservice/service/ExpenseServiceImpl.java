package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.models.request.ExpenseRequest;
import com.rkumar0206.mymexpenseservice.models.response.ExpenseResponse;
import com.rkumar0206.mymexpenseservice.repository.ExpenseRepository;
import com.rkumar0206.mymexpenseservice.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserContextService userContextService;

    @Override
    public ExpenseResponse create(ExpenseRequest request) {
        return null;
    }

    @Override
    public ExpenseResponse update(ExpenseRequest request) {
        return null;
    }

    @Override
    public Optional<Expense> getExpenseByKey(String key) {
        return Optional.empty();
    }

    @Override
    public Page<Expense> getUserExpenses(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Expense> getUserExpenseByPaymentMethodKey(Pageable pageable, List<String> paymentMethodKeys) {
        return null;
    }

    @Override
    public Page<Expense> getExpenseBetweenStartDateAndEndDate(Pageable pageable, Long startDate, Long endDate) {
        return null;
    }

    @Override
    public Page<Expense> getExpenseByCategoryKey(Pageable pageable, String categoryKey) {
        return null;
    }

    @Override
    public List<PaymentMethod> getAllPaymentMethodsOfUser() {
        return null;
    }

    @Override
    public List<PaymentMethod> getPaymentMethodsByKeys(List<String> keys) {
        return null;
    }
}
