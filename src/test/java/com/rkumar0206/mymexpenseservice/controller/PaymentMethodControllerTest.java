package com.rkumar0206.mymexpenseservice.controller;

import com.rkumar0206.mymexpenseservice.constantsAndEnums.Constants;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.exception.PaymentMethodException;
import com.rkumar0206.mymexpenseservice.models.request.PaymentMethodRequest;
import com.rkumar0206.mymexpenseservice.models.response.CustomResponse;
import com.rkumar0206.mymexpenseservice.service.PaymentMethodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodControllerTest {

    private final String tempPaymentMethodKey = "28754MXBcjhavcjhaacjh";
    @Mock
    private PaymentMethodService paymentMethodService;
    @InjectMocks
    private PaymentMethodController paymentMethodController;
    private PaymentMethod tempPaymentMethod;

    @BeforeEach
    void setUp() {

        tempPaymentMethod = new PaymentMethod(
                "njknkja",
                tempPaymentMethodKey,
                "sdckjhshsjhjhbs",
                new Date(),
                new Date(),
                "TEMP"
        );

    }


    @Test
    void createNewPaymentMethod_Success() {

        when(paymentMethodService.create(any())).thenReturn(tempPaymentMethod);

        ResponseEntity<CustomResponse<PaymentMethod>> actual = paymentMethodController.createNewPaymentMethod("kjbdkjabkab", new PaymentMethodRequest("TEMP", null));

        assertEquals(HttpStatusCode.valueOf(HttpStatus.CREATED.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertEquals(actual.getBody().getMessage(), Constants.SUCCESS);
    }

    @Test
    void createNewPaymentMethod_RequestNotValid_BAD_REQUEST() {

        //when(paymentMethodService.create(any())).thenReturn(tempPaymentMethod);

        ResponseEntity<CustomResponse<PaymentMethod>> actual = paymentMethodController.createNewPaymentMethod("ahkjbkabkb", new PaymentMethodRequest("", null));

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_BODY_NOT_VALID));
    }

    @Test
    void createNewPaymentMethod_SomeExceptionOccurred_Appropriate_Response() {

        when(paymentMethodService.create(any())).
                thenThrow(new PaymentMethodException(ErrorMessageConstants.PERMISSION_DENIED));

        ResponseEntity<CustomResponse<PaymentMethod>> actual = paymentMethodController.createNewPaymentMethod("qkajadbkbab", new PaymentMethodRequest("TEMP", null));

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());
    }


    @Test
    void updatePaymentMethod_Success() {

        when(paymentMethodService.update(any())).thenReturn(tempPaymentMethod);

        ResponseEntity<CustomResponse<PaymentMethod>> actual = paymentMethodController.updatePaymentMethod("alskhakbakbk", new PaymentMethodRequest("TEMP2", "akkajbkab"));

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertEquals(actual.getBody().getMessage(), Constants.SUCCESS);
    }

    @Test
    void updatePaymentMethod_RequestNotValid_BAD_REQUEST() {

        //when(paymentMethodService.update(any())).thenReturn(tempPaymentMethod);

        ResponseEntity<CustomResponse<PaymentMethod>> actual = paymentMethodController.updatePaymentMethod("alskhakbakbk", new PaymentMethodRequest("TEMP2", null));

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_BODY_NOT_VALID));
    }

    @Test
    void updatePaymentMethod_SomeExceptionOccurred_Appropriate_Response() {

        when(paymentMethodService.update(any())).
                thenThrow(new PaymentMethodException(ErrorMessageConstants.PERMISSION_DENIED));

        ResponseEntity<CustomResponse<PaymentMethod>> actual = paymentMethodController.updatePaymentMethod("qkajadbkbab", new PaymentMethodRequest("TEMP", "jhbhjafbjab"));

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());
    }


    @Test
    void getAllUserPaymentMethods_Success() {

        when(paymentMethodService.getAllPaymentMethodsOfUser()).thenReturn(List.of(tempPaymentMethod));

        ResponseEntity<CustomResponse<List<PaymentMethod>>> actual = paymentMethodController.getAllUserPaymentMethods("asjkajkcab");

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertEquals(actual.getBody().getMessage(), Constants.SUCCESS);
    }

    @Test
    void getAllUserPaymentMethods_SomeExceptionOccurred_Appropriate_Response() {

        when(paymentMethodService.getAllPaymentMethodsOfUser())
                .thenThrow(new PaymentMethodException(ErrorMessageConstants.PERMISSION_DENIED));

        ResponseEntity<CustomResponse<List<PaymentMethod>>> actual = paymentMethodController.getAllUserPaymentMethods("asjkajkcab");

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());

    }


    @Test
    void getAllUserPaymentMethodsByKeys_Success() {

        when(paymentMethodService.getPaymentMethodsByKeys(List.of(tempPaymentMethodKey))).thenReturn(List.of(tempPaymentMethod));

        ResponseEntity<CustomResponse<List<PaymentMethod>>> actual = paymentMethodController.getAllUserPaymentMethodsByKeys("asjkajkcab", List.of(tempPaymentMethodKey));

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertEquals(actual.getBody().getMessage(), Constants.SUCCESS);
    }

    @Test
    void getAllUserPaymentMethodsByKeys_PaymentMethodKeysNull_BAD_REQUEST() {


        ResponseEntity<CustomResponse<List<PaymentMethod>>> actual = paymentMethodController.getAllUserPaymentMethodsByKeys("asjkajkcab", null);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID));

    }


    @Test
    void getAllUserPaymentMethodsByKeys_SomeExceptionOccurred_Appropriate_Response() {

        when(paymentMethodService.getPaymentMethodsByKeys(List.of(tempPaymentMethodKey)))
                .thenThrow(new PaymentMethodException(ErrorMessageConstants.PERMISSION_DENIED));

        ResponseEntity<CustomResponse<List<PaymentMethod>>> actual = paymentMethodController.getAllUserPaymentMethodsByKeys("asjkajkcab", List.of(tempPaymentMethodKey));

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());
    }


    @Test
    void deletePaymentMethodsByKey_Success() {

        doNothing().when(paymentMethodService).deletePaymentMethod(anyString());

        ResponseEntity<CustomResponse<String>> actual = paymentMethodController.deletePaymentMethodsByKey("sdbskcsk", tempPaymentMethodKey);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()), actual.getStatusCode());
    }

    @Test
    void deletePaymentMethodsByKey_KeyNotValid_BAD_REQUEST() {

        //doNothing().when(paymentMethodService).deletePaymentMethod(anyString());

        ResponseEntity<CustomResponse<String>> actual = paymentMethodController.deletePaymentMethodsByKey("sdbskcsk", null);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID));

    }

    @Test
    void deletePaymentMethodsByKey_SomeExceptionOccurred_Appropriate_Response() {

        doThrow(new PaymentMethodException(ErrorMessageConstants.PERMISSION_DENIED))
                .when(paymentMethodService).deletePaymentMethod(anyString());

        ResponseEntity<CustomResponse<String>> actual = paymentMethodController.deletePaymentMethodsByKey("sdbskcsk", tempPaymentMethodKey);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());
    }
}