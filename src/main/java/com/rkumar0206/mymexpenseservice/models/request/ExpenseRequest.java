package com.rkumar0206.mymexpenseservice.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.RequestAction;
import com.rkumar0206.mymexpenseservice.utility.MymUtil;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ExpenseRequest {

    private Double amount;
    private String spentOn;
    private Long expenseDate;
    private String categoryKey;
    private List<String> paymentMethodsKeys;
    private List<String> newPaymentMethod;

    private String key; // for update


    @JsonIgnore
    public boolean isValid(RequestAction action) {

        boolean isValid = amount != null
                && amount >= 0.0
                && MymUtil.isValid(categoryKey)
                && expenseDate != null
                && expenseDate > 0L;


        if (isValid && newPaymentMethod != null && !newPaymentMethod.isEmpty()) {

            for (String paymentMethodName : newPaymentMethod) {

                isValid = MymUtil.isValid(paymentMethodName);
                if (!isValid) return isValid;
            }
        }

        if (isValid && action == RequestAction.UPDATE) {

            isValid = MymUtil.isValid(key);
        }

        return isValid;
    }
}
