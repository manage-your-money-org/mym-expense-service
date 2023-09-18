package com.rkumar0206.mymexpenseservice.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
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
    private List<PaymentMethod> paymentMethods;


    @JsonIgnore
    public boolean isValid() {

        boolean isValid = amount != null && MymStringUtil.isValid(categoryKey);

        if (isValid && paymentMethods != null) {

            for (PaymentMethod p : paymentMethods) {

                isValid = MymStringUtil.isValid(p.getPaymentMethodName());
            }
        }

        return isValid;
    }
}
