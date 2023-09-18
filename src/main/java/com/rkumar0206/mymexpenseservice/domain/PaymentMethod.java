package com.rkumar0206.mymexpenseservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// Sub-Document of Expense Collection
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentMethod {

    private String key;
    private String paymentMethodName;
}
