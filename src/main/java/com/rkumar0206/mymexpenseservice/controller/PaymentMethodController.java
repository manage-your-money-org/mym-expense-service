package com.rkumar0206.mymexpenseservice.controller;

import com.rkumar0206.mymexpenseservice.constantsAndEnums.Constants;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.Headers;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.RequestAction;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.exception.PaymentMethodException;
import com.rkumar0206.mymexpenseservice.models.request.PaymentMethodRequest;
import com.rkumar0206.mymexpenseservice.models.response.CustomResponse;
import com.rkumar0206.mymexpenseservice.service.PaymentMethodService;
import com.rkumar0206.mymexpenseservice.utility.MymUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mym/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping("/create")
    public ResponseEntity<CustomResponse<PaymentMethod>> createNewPaymentMethod(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestBody PaymentMethodRequest request
    ) {

        CustomResponse<PaymentMethod> response = new CustomResponse<>();

        try {

            if (!request.isValid(RequestAction.ADD))
                throw new PaymentMethodException(ErrorMessageConstants.REQUEST_BODY_NOT_VALID);

            PaymentMethod paymentMethod = paymentMethodService.create(request);

            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(paymentMethod);

        } catch (Exception ex) {

            MymUtil.setAppropriateResponseStatus(response, ex);
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @PutMapping("/update")
    public ResponseEntity<CustomResponse<PaymentMethod>> updatePaymentMethod(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestBody PaymentMethodRequest request
    ) {

        CustomResponse<PaymentMethod> response = new CustomResponse<>();

        try {

            if (!request.isValid(RequestAction.UPDATE))
                throw new PaymentMethodException(ErrorMessageConstants.REQUEST_BODY_NOT_VALID);

            PaymentMethod paymentMethod = paymentMethodService.update(request);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(paymentMethod);

        } catch (Exception ex) {

            MymUtil.setAppropriateResponseStatus(response, ex);
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @GetMapping
    public ResponseEntity<CustomResponse<List<PaymentMethod>>> getAllUserPaymentMethods(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId
    ) {

        CustomResponse<List<PaymentMethod>> response = new CustomResponse<>();

        try {

            List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethodsOfUser();

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(paymentMethods);

        } catch (Exception ex) {

            MymUtil.setAppropriateResponseStatus(response, ex);
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @GetMapping("/keys")
    public ResponseEntity<CustomResponse<List<PaymentMethod>>> getAllUserPaymentMethodsByKeys(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam("pm-keys") List<String> paymentMethodKeys
    ) {

        CustomResponse<List<PaymentMethod>> response = new CustomResponse<>();

        try {

            if (paymentMethodKeys == null || paymentMethodKeys.isEmpty())
                throw new PaymentMethodException(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID);

            List<PaymentMethod> paymentMethods = paymentMethodService.getPaymentMethodsByKeys(paymentMethodKeys);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(paymentMethods);

        } catch (Exception ex) {

            MymUtil.setAppropriateResponseStatus(response, ex);
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @DeleteMapping("/key")
    public ResponseEntity<CustomResponse<String>> deletePaymentMethodsByKey(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam("pm-key") String paymentMethodKey
    ) {

        CustomResponse<String> response = new CustomResponse<>();

        try {

            if (paymentMethodKey == null || MymUtil.isNotValid(paymentMethodKey))
                throw new PaymentMethodException(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID);

            paymentMethodService.deletePaymentMethod(paymentMethodKey);

            response.setStatus(HttpStatus.NO_CONTENT.value());
            response.setMessage(Constants.SUCCESS);

        } catch (Exception ex) {

            MymUtil.setAppropriateResponseStatus(response, ex);
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

}
