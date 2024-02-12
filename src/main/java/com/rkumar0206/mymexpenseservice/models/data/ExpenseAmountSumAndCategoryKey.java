package com.rkumar0206.mymexpenseservice.models.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseAmountSumAndCategoryKey {

    private Double totalExpenseAmount;
    private String categoryKey;
}
