package com.rkumar0206.mymexpenseservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "PaymentMethod")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentMethod {

    @JsonIgnore
    @Id
    private String id;

    private String key;
    private String uid;
    private Date created;
    private Date modified;
    private String paymentMethodName;
}
