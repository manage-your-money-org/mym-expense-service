package com.rkumar0206.mymexpenseservice.models.response;

import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.models.FeignClientResponses.ExpenseCategory;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ExpenseResponse {

    private Double amount;
    private Date created;
    private Date modified;
    private String spentOn;
    private String uid;
    private String key;
    private Long expenseDate;
    private ExpenseCategory category;
    private List<PaymentMethod> paymentMethods;
}
