package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSum;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSumAndCategoryKey;
import com.rkumar0206.mymexpenseservice.models.request.ExpenseRequest;
import com.rkumar0206.mymexpenseservice.models.response.ExpenseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import java.util.List;

public interface ExpenseService {

    ExpenseResponse create(ExpenseRequest request);

    ExpenseResponse update(ExpenseRequest request);

    ExpenseResponse getExpenseByKey(String key);

    Page<ExpenseResponse> getUserExpenses(Pageable pageable);

    Page<ExpenseResponse> getUserExpenseByPaymentMethodKey(Pageable pageable, List<String> paymentMethodKeys);

    Page<ExpenseResponse> getExpenseBetweenStartDateAndEndDate(Pageable pageable, Long startDate, Long endDate);

    Page<ExpenseResponse> getExpenseByCategoryKey(Pageable pageable, String categoryKey);

    Page<ExpenseResponse> getExpenseByCategoryKeyAndDateRange(Pageable pageable, String categoryKey, Long startDate, Long endDate);

    ExpenseAmountSum getTotalExpenseByCategoryKeys(List<String> categoryKeys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange);

    ExpenseAmountSum getTotalExpenseAmount(List<String> paymentMethodKeys, Pair<Long, Long> dateRange);

    List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategory(List<String> paymentMethodKeys, Pair<Long, Long> dateRange);

    List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByCategoryKeys(List<String> categoryKeys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange);

    List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByKeys(List<String> keys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange);


    void deleteExpense(String key);

    void deleteExpenseByCategoryKey(String categoryKey);

}
