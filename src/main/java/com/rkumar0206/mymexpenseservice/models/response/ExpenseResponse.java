package com.rkumar0206.mymexpenseservice.models.response;

import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ExpenseResponse {

    private Double amount;
    private Long created;
    private Long modified;
    private String spentOn;
    private String uid;
    private String key;
    private String categoryKey;
    private List<PaymentMethod> paymentMethods;
}
