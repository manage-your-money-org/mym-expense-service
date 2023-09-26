package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.exception.PaymentMethodException;
import com.rkumar0206.mymexpenseservice.models.UserInfo;
import com.rkumar0206.mymexpenseservice.models.request.PaymentMethodRequest;
import com.rkumar0206.mymexpenseservice.repository.PaymentMethodRepository;
import com.rkumar0206.mymexpenseservice.utility.MymUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserContextService userContextService;

    @Override
    public PaymentMethod create(PaymentMethodRequest paymentMethodRequest) {

        String uid = getUserInfo().getUid();

        Optional<PaymentMethod> paymentMethodDB = paymentMethodRepository.findByUidAndPaymentMethodName(
                uid, paymentMethodRequest.getPaymentMethodName().trim()
        );

        if (paymentMethodDB.isPresent())
            throw new PaymentMethodException(ErrorMessageConstants.PAYMENT_METHOD_WITH_THIS_NAME_ALREADY_PRESENT);

        PaymentMethod paymentMethod = new PaymentMethod(
                null, MymUtil.createNewKey(uid), uid, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), paymentMethodRequest.getPaymentMethodName().trim());

        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public PaymentMethod update(PaymentMethodRequest paymentMethodRequest) {

        String uid = getUserInfo().getUid();

        Optional<PaymentMethod> paymentMethodDB = paymentMethodRepository.findByKey(
                paymentMethodRequest.getKey()
        );

        if (paymentMethodDB.isEmpty())
            throw new PaymentMethodException(ErrorMessageConstants.NO_PAYMENT_METHOD_FOUND_ERROR);

        if (!paymentMethodDB.get().getUid().equals(uid))
            throw new PaymentMethodException(ErrorMessageConstants.PERMISSION_DENIED);

        if (paymentMethodDB.get().getPaymentMethodName().equals(paymentMethodRequest.getPaymentMethodName().trim()))
            return paymentMethodDB.get();

        Optional<PaymentMethod> paymentMethodByNameDB = paymentMethodRepository.findByUidAndPaymentMethodName(
                uid, paymentMethodRequest.getPaymentMethodName().trim()
        );

        if (paymentMethodByNameDB.isPresent())
            throw new PaymentMethodException(ErrorMessageConstants.PAYMENT_METHOD_WITH_THIS_NAME_ALREADY_PRESENT);

        paymentMethodDB.get().updateFields(paymentMethodRequest.getPaymentMethodName());

        return paymentMethodRepository.save(paymentMethodDB.get());
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

        String uid = getUserInfo().getUid();

        Optional<PaymentMethod> paymentMethodDB = paymentMethodRepository.findByKey(key);

        if (paymentMethodDB.isPresent()) {

            if (!paymentMethodDB.get().getUid().equals(uid))
                throw new PaymentMethodException(ErrorMessageConstants.PERMISSION_DENIED);

            paymentMethodRepository.delete(paymentMethodDB.get());
        }
    }

    private UserInfo getUserInfo() {

        return userContextService.getUserInfo();
    }

}
