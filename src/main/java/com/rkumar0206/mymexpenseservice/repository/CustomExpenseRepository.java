package com.rkumar0206.mymexpenseservice.repository;

import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSum;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSumAndCategoryKey;
import org.springframework.data.util.Pair;

import java.util.List;

public interface CustomExpenseRepository {

    ExpenseAmountSum getTotalExpenseAmountByUid(String uid, List<String> paymentMethodKeys, Pair<Long, Long> dateRange);

    ExpenseAmountSum getTotalExpenseByUidAndCategoryKeys(String uid, List<String> categoryKeys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange);

    List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByUid(String uid, List<String> paymentMethodKeys, Pair<Long, Long> dateRange);

    List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByUidAndKeys(String uid, List<String> keys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange);

    List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByUidAndCategoryKeys(String uid, List<String> categoryKeys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange);
}
