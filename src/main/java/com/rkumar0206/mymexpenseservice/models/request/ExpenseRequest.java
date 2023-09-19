package com.rkumar0206.mymexpenseservice.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.RequestAction;
import com.rkumar0206.mymexpenseservice.utility.MymStringUtil;
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

    private String key; // for update


    @JsonIgnore
    public boolean isValid(RequestAction action) {

        boolean isValid = amount != null && MymStringUtil.isValid(categoryKey);

        if (isValid && paymentMethodsKeys != null) {

            isValid = !paymentMethodsKeys.isEmpty();
        }

        if (isValid && action == RequestAction.UPDATE) {

            isValid = MymStringUtil.isValid(key);
        }

        return isValid;
    }
}
