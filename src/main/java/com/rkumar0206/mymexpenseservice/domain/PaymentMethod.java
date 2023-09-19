package com.rkumar0206.mymexpenseservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PaymentMethod")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentMethod {

    @Id
    private String id;

    private String key;
    private String uid;
    private Long created;
    private Long modified;
    private String paymentMethodName;
}
