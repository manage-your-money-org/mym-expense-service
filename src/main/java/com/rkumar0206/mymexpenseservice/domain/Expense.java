package com.rkumar0206.mymexpenseservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rkumar0206.mymexpenseservice.models.request.ExpenseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(collection = "Expense")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Expense {

    @JsonIgnore
    @Id
    private String id;

    private Double amount;
    private Date created;
    private Date modified;
    private Long expenseDate;
    private String spentOn;
    private String uid;
    private String key;
    private String categoryKey;
    private List<String> paymentMethodKeys;

    public void updateFields(ExpenseRequest request, List<String> finalPaymentMethodKeys) {

        this.setAmount(request.getAmount());
        this.setExpenseDate(request.getExpenseDate());
        this.setSpentOn(request.getSpentOn());
        this.setPaymentMethodKeys(finalPaymentMethodKeys);
        this.setModified(new Date(System.currentTimeMillis()));
        this.setCategoryKey(request.getCategoryKey());
    }
}
