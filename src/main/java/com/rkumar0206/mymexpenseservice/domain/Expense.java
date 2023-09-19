package com.rkumar0206.mymexpenseservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private Long createdDB;
    private Long modifiedDB;
    private Long expenseDate;
    private String spentOn;
    private String uid;
    private String key;
    private String categoryKey;
    private List<String> paymentMethodKeys;

}
