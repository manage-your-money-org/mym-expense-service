package com.rkumar0206.mymexpenseservice.repository;

import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends MongoRepository<PaymentMethod, String> {

    Optional<PaymentMethod> findByKey(String key);

    Optional<PaymentMethod> findByUidAndPaymentMethodName(String uid, String paymentMethodName);

    List<PaymentMethod> findByUid(String uid);

    List<PaymentMethod> findByKeyIn(List<String> keys);
}
