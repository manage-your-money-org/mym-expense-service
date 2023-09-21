package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.models.UserInfo;
import com.rkumar0206.mymexpenseservice.models.request.PaymentMethodRequest;
import com.rkumar0206.mymexpenseservice.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserContextService userContextService;

    @Override
    public PaymentMethod create(PaymentMethodRequest paymentMethodRequest) {
        return null;
    }

    @Override
    public PaymentMethod update(PaymentMethodRequest paymentMethodRequest) {
        return null;
    }

    @Override
    public List<PaymentMethod> getAllPaymentMethodsOfUser() {

        String uid = getUserInfo().getUid();

        return paymentMethodRepository.findByUid(uid);
    }

    @Override
    public List<PaymentMethod> getPaymentMethodsByKeys(List<String> keys) {

        String uid = getUserInfo().getUid();
        return paymentMethodRepository.findByUidAndKeyIn(uid, keys);
    }

    @Override
    public void deletePaymentMethod(String key) {

    }

    private UserInfo getUserInfo() {

        return userContextService.getUserInfo();
    }

}
