package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSum;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSumAndCategoryKey;
import com.rkumar0206.mymexpenseservice.models.request.ExpenseRequest;
import com.rkumar0206.mymexpenseservice.models.request.FilterRequest;
import com.rkumar0206.mymexpenseservice.models.response.ExpenseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import java.util.List;

public interface ExpenseService {

    ExpenseResponse create(ExpenseRequest request);

    ExpenseResponse update(ExpenseRequest request);

    ExpenseResponse getExpenseByKey(String key);

    //Page<ExpenseResponse> getUserExpenses(Pageable pageable);

    Page<ExpenseResponse> getUserExpenses(
            Pageable pageable,
            FilterRequest filterRequest
    );

    ExpenseAmountSum getTotalExpenseAmount(FilterRequest filterRequest);

    List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategory(FilterRequest filterRequest);

    List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByKeys(List<String> keys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange);


    void deleteExpense(String key);

    void deleteExpenseByCategoryKey(String categoryKey);

}
