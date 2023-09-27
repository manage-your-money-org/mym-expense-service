package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.models.request.PaymentMethodRequest;

import java.util.List;

public interface PaymentMethodService {

    PaymentMethod create(PaymentMethodRequest paymentMethodRequest);

    PaymentMethod update(PaymentMethodRequest paymentMethodRequest);

    List<PaymentMethod> getAllPaymentMethodsOfUser();

    List<PaymentMethod> getPaymentMethodsByKeys(List<String> keys);

    void deletePaymentMethod(String key);

}
