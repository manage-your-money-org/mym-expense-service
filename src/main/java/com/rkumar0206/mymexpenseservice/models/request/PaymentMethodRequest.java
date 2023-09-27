package com.rkumar0206.mymexpenseservice.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.RequestAction;
import com.rkumar0206.mymexpenseservice.utility.MymUtil;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PaymentMethodRequest {

    private String paymentMethodName;
    private String key; // only for update

    @JsonIgnore
    public boolean isValid(RequestAction action) {

        boolean isValid = MymUtil.isValid(paymentMethodName);

        if (isValid && action == RequestAction.UPDATE) {

            isValid = MymUtil.isValid(key);
        }
        return isValid;
    }

}
