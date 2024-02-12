package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.exception.PaymentMethodException;
import com.rkumar0206.mymexpenseservice.models.UserInfo;
import com.rkumar0206.mymexpenseservice.models.request.PaymentMethodRequest;
import com.rkumar0206.mymexpenseservice.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    private final String tempPaymentMethodKey = "aslnakjcbahbhjavhgahga";
    @Mock
    private PaymentMethodRepository paymentMethodRepository;
    @Mock
    private UserContextService userContextService;
    @InjectMocks
    private PaymentMethodServiceImpl paymentMethodService;
    private PaymentMethod tempPaymentMethod;
    private UserInfo tempUserInfo;
    private String uid = "asdjcbsbsbjshbjshbjhsbjsbjsb";

    @BeforeEach
    void setUp() {

        tempUserInfo = new UserInfo(
                "Temp Name",
                "tempEmail@test.gmail.com",
                uid,
                true
        );

        tempPaymentMethod = new PaymentMethod(
                "sjnkjsbksbkcbs",
                tempPaymentMethodKey,
                uid,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                "TEMP1"
        );


        Mockito.when(userContextService.getUserInfo()).thenReturn(tempUserInfo);
    }

    @Test
    void create_Success() {


        when(paymentMethodRepository.findByUidAndPaymentMethodName(uid, tempPaymentMethod.getPaymentMethodName()))
                .thenReturn(Optional.empty());

        PaymentMethodRequest request = new PaymentMethodRequest(tempPaymentMethod.getPaymentMethodName(), null);

        paymentMethodService.create(request);

        ArgumentCaptor<PaymentMethod> paymentMethodArgumentCaptor = ArgumentCaptor.forClass(PaymentMethod.class);
        verify(paymentMethodRepository).save(paymentMethodArgumentCaptor.capture());

        PaymentMethod actual = paymentMethodArgumentCaptor.getValue();

        assertEquals(tempPaymentMethod.getPaymentMethodName(), actual.getPaymentMethodName());
        assertNotNull(actual.getKey());
    }

    @Test
    void create_PaymentNameAlreadyPresent_ExceptionThrown() {


        when(paymentMethodRepository.findByUidAndPaymentMethodName(uid, tempPaymentMethod.getPaymentMethodName()))
                .thenReturn(Optional.of(tempPaymentMethod));

        PaymentMethodRequest request = new PaymentMethodRequest(tempPaymentMethod.getPaymentMethodName(), null);

        assertThatThrownBy(() -> paymentMethodService.create(request))
                .isInstanceOf(PaymentMethodException.class)
                .hasMessage(ErrorMessageConstants.PAYMENT_METHOD_WITH_THIS_NAME_ALREADY_PRESENT);
    }


    @Test
    void update_Success() {

        String oldPmName = tempPaymentMethod.getPaymentMethodName();

        when(paymentMethodRepository.findByKey(tempPaymentMethodKey)).thenReturn(Optional.of(tempPaymentMethod));
        when(paymentMethodRepository.findByUidAndPaymentMethodName(anyString(), anyString())).thenReturn(Optional.empty());

        PaymentMethodRequest request = new PaymentMethodRequest("TEMP_2_TEST", tempPaymentMethodKey);

        paymentMethodService.update(request);

        ArgumentCaptor<PaymentMethod> paymentMethodArgumentCaptor = ArgumentCaptor.forClass(PaymentMethod.class);
        verify(paymentMethodRepository).save(paymentMethodArgumentCaptor.capture());

        PaymentMethod actual = paymentMethodArgumentCaptor.getValue();

        assertNotEquals(oldPmName, actual.getPaymentMethodName());
        assertEquals(request.getPaymentMethodName(), actual.getPaymentMethodName());
    }

    @Test
    void update_NoChangeFound_Success() {

        when(paymentMethodRepository.findByKey(tempPaymentMethodKey)).thenReturn(Optional.of(tempPaymentMethod));

        PaymentMethodRequest request = new PaymentMethodRequest(tempPaymentMethod.getPaymentMethodName(), tempPaymentMethodKey);

        paymentMethodService.update(request);

        verify(paymentMethodRepository, times(0)).save(any());
    }


    @Test
    void update_Not_Found_ExceptionThrown() {

        when(paymentMethodRepository.findByKey(tempPaymentMethodKey)).thenReturn(Optional.empty());

        PaymentMethodRequest request = new PaymentMethodRequest("TEMP_2_TEST", tempPaymentMethodKey);

        assertThatThrownBy(() -> paymentMethodService.update(request))
                .isInstanceOf(PaymentMethodException.class)
                .hasMessage(ErrorMessageConstants.NO_PAYMENT_METHOD_FOUND_ERROR);
    }

    @Test
    void update_PaymentMethodWithSameName_AlreadyPresent_ExceptionThrown() {

        when(paymentMethodRepository.findByKey(tempPaymentMethodKey)).thenReturn(Optional.of(tempPaymentMethod));
        when(paymentMethodRepository.findByUidAndPaymentMethodName(anyString(), anyString())).thenReturn(Optional.of(new PaymentMethod()));

        PaymentMethodRequest request = new PaymentMethodRequest("TEMP_2_TEST", tempPaymentMethodKey);

        assertThatThrownBy(() -> paymentMethodService.update(request))
                .isInstanceOf(PaymentMethodException.class)
                .hasMessage(ErrorMessageConstants.PAYMENT_METHOD_WITH_THIS_NAME_ALREADY_PRESENT);
    }

    @Test
    void update_UidMismatch_ExceptionThrown() {

        tempPaymentMethod.setUid("jvkjsbvhsbsbj");

        when(paymentMethodRepository.findByKey(tempPaymentMethodKey)).thenReturn(Optional.of(tempPaymentMethod));

        PaymentMethodRequest request = new PaymentMethodRequest("TEMP_2_TEST", tempPaymentMethodKey);

        assertThatThrownBy(() -> paymentMethodService.update(request))
                .isInstanceOf(PaymentMethodException.class)
                .hasMessage(ErrorMessageConstants.PERMISSION_DENIED);
    }


    @Test
    void getAllPaymentMethodsOfUser_Success() {

        when(paymentMethodRepository.findByUid(uid)).thenReturn(List.of(tempPaymentMethod));

        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethodsOfUser();

        assertNotEquals(0, paymentMethods.size());
        assertEquals(tempPaymentMethod.getPaymentMethodName(), paymentMethods.get(0).getPaymentMethodName());
    }

    @Test
    void getPaymentMethodsByKeys_Success() {

        when(paymentMethodRepository.findByUidAndKeyIn(uid, List.of(tempPaymentMethodKey))).thenReturn(List.of(tempPaymentMethod));

        List<PaymentMethod> paymentMethods = paymentMethodService.getPaymentMethodsByKeys(List.of(tempPaymentMethodKey));

        assertNotEquals(0, paymentMethods.size());
        assertEquals(tempPaymentMethod.getPaymentMethodName(), paymentMethods.get(0).getPaymentMethodName());

    }

    @Test
    void deletePaymentMethod_Success() {

        doNothing().when(paymentMethodRepository).delete(any());
        when(paymentMethodRepository.findByKey(anyString())).thenReturn(Optional.of(tempPaymentMethod));

        paymentMethodService.deletePaymentMethod(tempPaymentMethodKey);

        verify(paymentMethodRepository, times(1)).delete(tempPaymentMethod);
    }

    @Test
    void deletePaymentMethod_UidMismatch_Success() {

        tempPaymentMethod.setUid("sjbvsbsbsbbsjh");
        when(paymentMethodRepository.findByKey(anyString())).thenReturn(Optional.of(tempPaymentMethod));

        assertThatThrownBy(() -> paymentMethodService.deletePaymentMethod(tempPaymentMethodKey))
                .isInstanceOf(PaymentMethodException.class)
                .hasMessage(ErrorMessageConstants.PERMISSION_DENIED);

    }

}