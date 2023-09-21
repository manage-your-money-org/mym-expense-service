package com.rkumar0206.mymexpenseservice.utility;

import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.models.response.ExpenseResponse;

import java.util.List;

public class ModelMapper {

    public static ExpenseResponse buildExpenseResponse(Expense expense, List<PaymentMethod> paymentMethods) {

        return ExpenseResponse.builder()
                .uid(expense.getUid())
                .key(expense.getKey())
                .amount(expense.getAmount())
                .categoryKey(expense.getCategoryKey())
                .created(expense.getCreated())
                .modified(expense.getModified())
                .spentOn(expense.getSpentOn())
                .expenseDate(expense.getExpenseDate())
                .paymentMethods(paymentMethods)
                .build();

    }

}
