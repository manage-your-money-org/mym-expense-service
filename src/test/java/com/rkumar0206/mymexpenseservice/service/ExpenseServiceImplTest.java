package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.exception.ExpenseException;
import com.rkumar0206.mymexpenseservice.feignClient.ExpenseCategoryAPI;
import com.rkumar0206.mymexpenseservice.models.FeignClientResponses.ExpenseCategory;
import com.rkumar0206.mymexpenseservice.models.UserInfo;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSum;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSumAndCategoryKey;
import com.rkumar0206.mymexpenseservice.models.request.ExpenseRequest;
import com.rkumar0206.mymexpenseservice.models.request.FilterRequest;
import com.rkumar0206.mymexpenseservice.models.response.CustomResponse;
import com.rkumar0206.mymexpenseservice.models.response.ExpenseResponse;
import com.rkumar0206.mymexpenseservice.repository.ExpenseRepository;
import com.rkumar0206.mymexpenseservice.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    private final String tempPaymentMethodKey = "aslnakjcbahbhjavhgahga";
    private final String tempCategoryKey = "asbajhjaavavvajavvah";
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private PaymentMethodRepository paymentMethodRepository;
    @Mock
    private UserContextService userContextService;
    @Mock
    private ExpenseCategoryAPI expenseCategoryAPI;
    @InjectMocks
    private ExpenseServiceImpl expenseService;
    private UserInfo tempUserInfo;
    private Expense tempExpense;
    private PaymentMethod tempPaymentMethod;
    private Pageable pageable;

    @BeforeEach
    void setup() {

        String uid = "rrrrr_" + UUID.randomUUID();

        tempUserInfo = new UserInfo(
                "Temp Name",
                "tempEmail@test.gmail.com",
                uid,
                true
        );

        Mockito.when(userContextService.getUserInfo()).thenReturn(tempUserInfo);

        tempExpense = new Expense(
                "sjcbshbsbjhsgvshvhgsvg",
                50000.0,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                System.currentTimeMillis(),
                "somthing amazing",
                uid,
                "asjcbakbababjhajh",
                tempCategoryKey,
                Arrays.asList("sckjsbhsbjhsscakjbb", "acjbahjbajhbajh")
        );

        tempPaymentMethod = new PaymentMethod(
                "sjnkjsbksbkcbs",
                tempPaymentMethodKey,
                uid,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                "TEMP1"
        );

        pageable = PageRequest.of(0, 50);
    }

    @Test
    void create_Success() {

        when(paymentMethodRepository.findByUidAndKeyIn(
                tempUserInfo.getUid(), List.of(tempPaymentMethodKey)
        )).thenReturn(List.of(tempPaymentMethod));

        when(paymentMethodRepository.findByUidAndPaymentMethodName(
                tempUserInfo.getUid(), "TEMP2"
        )).thenReturn(Optional.empty());

        ExpenseRequest request = new ExpenseRequest(
                6000.0,
                "something amazing",
                System.currentTimeMillis(),
                tempCategoryKey,
                List.of(tempPaymentMethodKey),
                List.of("TEMP2"),
                null
        );

        mockExpenseCategoryAPIForKey();
        //----------------
        // This is only for ModelMapper method does not fail
        when(paymentMethodRepository.save(any())).thenReturn(tempPaymentMethod);
        when(expenseRepository.save(any())).thenReturn(tempExpense);
        //------------------

        expenseService.create(request);

        ArgumentCaptor<PaymentMethod> paymentMethodArgumentCaptor = ArgumentCaptor.forClass(PaymentMethod.class);

        verify(paymentMethodRepository).save(paymentMethodArgumentCaptor.capture());

        PaymentMethod newPaymentMethodWithTEMP2 = paymentMethodArgumentCaptor.getValue();

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);

        verify(expenseRepository).save(expenseArgumentCaptor.capture());

        Expense actualExpense = expenseArgumentCaptor.getValue();

        assertEquals(request.getAmount(), actualExpense.getAmount());
        assertEquals(request.getSpentOn(), actualExpense.getSpentOn());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());
        assertNotNull(actualExpense.getKey());
        assertNotEquals("", actualExpense.getKey());
        assertNotNull(actualExpense.getCreated());
        assertNotNull(actualExpense.getModified());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());
        assertEquals(List.of(tempPaymentMethodKey, newPaymentMethodWithTEMP2.getKey()), actualExpense.getPaymentMethodKeys());

    }

    @Test
    void create_whenNoPaymentMethodPassed_Success() {

        ExpenseRequest request = new ExpenseRequest(
                6000.0,
                "something amazing",
                System.currentTimeMillis(),
                tempCategoryKey,
                null,
                null,
                null
        );

        mockExpenseCategoryAPIForKey();

        //----------------
        // This is only for ModelMapper method does not fail
        when(expenseRepository.save(any())).thenReturn(tempExpense);
        //------------------

        expenseService.create(request);

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);

        verify(expenseRepository).save(expenseArgumentCaptor.capture());

        Expense actualExpense = expenseArgumentCaptor.getValue();

        assertEquals(request.getAmount(), actualExpense.getAmount());
        assertEquals(request.getSpentOn(), actualExpense.getSpentOn());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());
        assertNotNull(actualExpense.getKey());
        assertNotEquals("", actualExpense.getKey());
        assertNotNull(actualExpense.getCreated());
        assertNotNull(actualExpense.getModified());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());

        assertEquals(new ArrayList<String>(), actualExpense.getPaymentMethodKeys());

    }

    @Test
    void create_whenOnlyPaymentMethodKeyListIsPassed_Success() {

        when(paymentMethodRepository.findByUidAndKeyIn(
                tempUserInfo.getUid(), List.of(tempPaymentMethodKey)
        )).thenReturn(List.of(tempPaymentMethod));

        ExpenseRequest request = new ExpenseRequest(
                6000.0,
                "something amazing",
                System.currentTimeMillis(),
                tempCategoryKey,
                List.of(tempPaymentMethodKey),
                null,
                null
        );

        mockExpenseCategoryAPIForKey();

        //----------------
        // This is only for ModelMapper method does not fail
        when(expenseRepository.save(any())).thenReturn(tempExpense);
        //------------------

        expenseService.create(request);

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);

        verify(expenseRepository).save(expenseArgumentCaptor.capture());

        Expense actualExpense = expenseArgumentCaptor.getValue();

        assertEquals(request.getAmount(), actualExpense.getAmount());
        assertEquals(request.getSpentOn(), actualExpense.getSpentOn());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());
        assertNotNull(actualExpense.getKey());
        assertNotEquals("", actualExpense.getKey());
        assertNotNull(actualExpense.getCreated());
        assertNotNull(actualExpense.getModified());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());

        assertEquals(List.of(tempPaymentMethodKey), actualExpense.getPaymentMethodKeys());

    }

    @Test
    void create_whenOnlyNewPaymentMethodListIsPassed_NewPaymentMethodShouldBeSavedToRepository_Success() {

        when(paymentMethodRepository.findByUidAndPaymentMethodName(
                tempUserInfo.getUid(), "TEMP2"
        )).thenReturn(Optional.empty());

        ExpenseRequest request = new ExpenseRequest(
                6000.0,
                "something amazing",
                System.currentTimeMillis(),
                tempCategoryKey,
                null,
                List.of("TEMP2"),
                null
        );

        mockExpenseCategoryAPIForKey();

        //----------------
        // This is only for ModelMapper method does not fail
        when(paymentMethodRepository.save(any())).thenReturn(tempPaymentMethod);
        when(expenseRepository.save(any())).thenReturn(tempExpense);
        //------------------

        expenseService.create(request);

        ArgumentCaptor<PaymentMethod> paymentMethodArgumentCaptor = ArgumentCaptor.forClass(PaymentMethod.class);

        verify(paymentMethodRepository).save(paymentMethodArgumentCaptor.capture());

        PaymentMethod newPaymentMethodWithTEMP2 = paymentMethodArgumentCaptor.getValue();

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);

        verify(expenseRepository).save(expenseArgumentCaptor.capture());

        Expense actualExpense = expenseArgumentCaptor.getValue();

        assertEquals(request.getAmount(), actualExpense.getAmount());
        assertEquals(request.getSpentOn(), actualExpense.getSpentOn());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());
        assertNotNull(actualExpense.getKey());
        assertNotEquals("", actualExpense.getKey());
        assertNotNull(actualExpense.getCreated());
        assertNotNull(actualExpense.getModified());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());

        assertEquals(List.of(newPaymentMethodWithTEMP2.getKey()), actualExpense.getPaymentMethodKeys());
    }

    @Test
    void create_whenNewPaymentMethodNameAlreadyExistInDb_Success() {

        PaymentMethod temp2PaymentMethod = new PaymentMethod(
                "sknslncls",
                "sajbakjbckabkabkabc",
                tempUserInfo.getUid(),
                new Date(),
                new Date(),
                "TEMP2"
        );

        when(paymentMethodRepository.findByUidAndPaymentMethodName(
                tempUserInfo.getUid(), "TEMP2"
        )).thenReturn(Optional.of(temp2PaymentMethod));

        ExpenseRequest request = new ExpenseRequest(
                6000.0,
                "something amazing",
                System.currentTimeMillis(),
                tempCategoryKey,
                null,
                List.of("TEMP2"),
                null
        );

        mockExpenseCategoryAPIForKey();
        //----------------
        // This is only for ModelMapper method does not fail
        when(expenseRepository.save(any())).thenReturn(tempExpense);
        //------------------

        expenseService.create(request);

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(expenseArgumentCaptor.capture());
        Expense actualExpense = expenseArgumentCaptor.getValue();

        assertEquals(request.getAmount(), actualExpense.getAmount());
        assertEquals(request.getSpentOn(), actualExpense.getSpentOn());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());
        assertNotNull(actualExpense.getKey());
        assertNotEquals("", actualExpense.getKey());
        assertNotNull(actualExpense.getCreated());
        assertNotNull(actualExpense.getModified());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());

        assertEquals(List.of(temp2PaymentMethod.getKey()), actualExpense.getPaymentMethodKeys());
    }


    @Test
    void update_Success() {

        when(paymentMethodRepository.findByUidAndKeyIn(
                tempUserInfo.getUid(), List.of(tempPaymentMethodKey)
        )).thenReturn(List.of(tempPaymentMethod));

        when(paymentMethodRepository.findByUidAndPaymentMethodName(
                tempUserInfo.getUid(), "TEMP2"
        )).thenReturn(Optional.empty());

        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(Optional.of(tempExpense));

        ExpenseRequest request = new ExpenseRequest(
                6000.0,
                "something amazing updated",
                tempExpense.getExpenseDate(),
                tempCategoryKey,
                List.of(tempPaymentMethodKey),
                List.of("TEMP2"),
                tempExpense.getKey()
        );

        mockExpenseCategoryAPIForKey();

        //----------------
        // This is only for ModelMapper method does not fail
        when(paymentMethodRepository.save(any())).thenReturn(tempPaymentMethod);
        when(expenseRepository.save(any())).thenReturn(tempExpense);
        //------------------

        expenseService.update(request);

        ArgumentCaptor<PaymentMethod> paymentMethodArgumentCaptor = ArgumentCaptor.forClass(PaymentMethod.class);
        verify(paymentMethodRepository).save(paymentMethodArgumentCaptor.capture());
        PaymentMethod newPaymentMethodWithTEMP2 = paymentMethodArgumentCaptor.getValue();

        ArgumentCaptor<Expense> expenseArgumentCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(expenseArgumentCaptor.capture());
        Expense actualExpense = expenseArgumentCaptor.getValue();

        assertEquals(request.getAmount(), actualExpense.getAmount());
        assertEquals(request.getSpentOn(), actualExpense.getSpentOn());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());
        assertNotNull(actualExpense.getKey());
        assertNotEquals("", actualExpense.getKey());
        assertNotNull(actualExpense.getCreated());
        assertNotNull(actualExpense.getModified());
        assertEquals(request.getCategoryKey(), actualExpense.getCategoryKey());
        assertEquals(List.of(tempPaymentMethodKey, newPaymentMethodWithTEMP2.getKey()), actualExpense.getPaymentMethodKeys());

    }

    private void mockExpenseCategoryAPIForKey() {

        when(userContextService.getAuthorizationToken()).thenReturn("shvjjshvhjs");
        when(userContextService.getCorrelationId()).thenReturn("abjhababja");
        when(userContextService.getUserInfoHeaderValue()).thenReturn("abhabhjabjhajh");

        CustomResponse<ExpenseCategory> expenseCategoryResponse = new CustomResponse<>();
        expenseCategoryResponse.setBody(ExpenseCategory.builder().key(tempCategoryKey).build());
        when(expenseCategoryAPI.getExpenseCategoryByKey(
                anyString(),
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(new ResponseEntity<>(expenseCategoryResponse, HttpStatus.OK));
    }

    private void mockExpenseCategoryAPIForGettingAllCategories() {

        when(userContextService.getAuthorizationToken()).thenReturn("shvjjshvhjs");
        when(userContextService.getCorrelationId()).thenReturn("abjhababja");
        when(userContextService.getUserInfoHeaderValue()).thenReturn("abhabhjabjhajh");

        CustomResponse<Page<ExpenseCategory>> expenseCategoryResponse = new CustomResponse<>();
        expenseCategoryResponse.setBody(new PageImpl<>(List.of(ExpenseCategory.builder().key(tempCategoryKey).build())));
        when(expenseCategoryAPI.getExpenseCategoryOfUser(
                anyString(),
                anyString(),
                anyString(),
                any()
        )).thenReturn(new ResponseEntity<>(expenseCategoryResponse, HttpStatus.OK));
    }


    @Test
    void update_noExpenseFound_ExceptionThrown() {

        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(Optional.empty());

        ExpenseRequest request = new ExpenseRequest(
                6000.0,
                "something amazing updated",
                tempExpense.getExpenseDate(),
                tempCategoryKey,
                List.of(tempPaymentMethodKey),
                List.of("TEMP2"),
                tempExpense.getKey()
        );


        assertThatThrownBy(() -> expenseService.update(request))
                .isInstanceOf(ExpenseException.class)
                .hasMessage(ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR);

    }

    @Test
    void update_uidDoesNotMatch_ExceptionThrown() {

        tempExpense.setUid("bchcbhjvjhavjavjvajvj");
        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(Optional.of(tempExpense));

        ExpenseRequest request = new ExpenseRequest(
                6000.0,
                "something amazing updated",
                tempExpense.getExpenseDate(),
                tempCategoryKey,
                List.of(tempPaymentMethodKey),
                List.of("TEMP2"),
                tempExpense.getKey()
        );


        assertThatThrownBy(() -> expenseService.update(request))
                .isInstanceOf(ExpenseException.class)
                .hasMessage(ErrorMessageConstants.PERMISSION_DENIED);

    }


    @Test
    void getExpenseByKey_Success() {

        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(Optional.of(tempExpense));

        when(paymentMethodRepository.findByUidAndKeyIn(
                tempUserInfo.getUid(), tempExpense.getPaymentMethodKeys()
        )).thenReturn(List.of(new PaymentMethod(), new PaymentMethod()));

        mockExpenseCategoryAPIForKey();

        ExpenseResponse actual = expenseService.getExpenseByKey(tempExpense.getKey());

        assertEquals(tempExpense.getAmount(), actual.getAmount());
        //assertEquals(tempExpense.getCategoryKey(), actual.getCategoryKey());
        assertEquals(tempExpense.getExpenseDate(), actual.getExpenseDate());

    }

    @Test
    void getExpenseByKey_NoPaymentMethodFoundInExpense_Success() {

        tempExpense.setPaymentMethodKeys(new ArrayList<>());
        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(Optional.of(tempExpense));
        mockExpenseCategoryAPIForKey();

        ExpenseResponse actual = expenseService.getExpenseByKey(tempExpense.getKey());

        assertEquals(tempExpense.getAmount(), actual.getAmount());
        //assertEquals(tempExpense.getCategoryKey(), actual.getCategoryKey());
        assertEquals(tempExpense.getExpenseDate(), actual.getExpenseDate());
        assertNotNull(actual.getPaymentMethods());
    }


    @Test
    void getExpenseByKey_NoExpenseFound_ExceptionThrown() {

        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.getExpenseByKey(tempExpense.getKey()))
                .isInstanceOf(ExpenseException.class)
                .hasMessage(ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR);

    }

    @Test
    void getExpenseByKey_UidNotMatch_ExceptionThrown() {

        tempExpense.setUid("jsnckjabcab");
        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(Optional.of(tempExpense));

        assertThatThrownBy(() -> expenseService.getExpenseByKey(tempExpense.getKey()))
                .isInstanceOf(ExpenseException.class)
                .hasMessage(ErrorMessageConstants.PERMISSION_DENIED);

    }


    @Test
    void getUserExpenses_NoFiltersAreNull_Success() {

        tempExpense.setPaymentMethodKeys(List.of(tempPaymentMethodKey));
        Page<Expense> expensePage = new PageImpl<>(
                Collections.singletonList(tempExpense)
        );

        when(expenseRepository.findByUidAndCategoryKeyInAndPaymentMethodKeysInAndExpenseDateBetween(
                tempUserInfo.getUid(), List.of(tempCategoryKey), List.of(tempPaymentMethodKey), 0L, 2487687L, pageable)
        ).thenReturn(
                expensePage
        );
        mockExpenseCategoryAPIForGettingAllCategories();

        tempPaymentMethod.setKey(tempPaymentMethodKey);
        when(paymentMethodRepository.findByUid(anyString())).thenReturn(List.of(tempPaymentMethod));

        FilterRequest filterRequest = FilterRequest.builder()
                .categoryKeys(List.of(tempCategoryKey))
                .paymentMethodKeys(List.of(tempPaymentMethodKey))
                .dateRange(Pair.of(0L, 2487687L))
                .build();

        Page<ExpenseResponse> actual = expenseService.getUserExpenses(pageable, filterRequest);

        assertEquals(expensePage.getTotalElements(), actual.getTotalElements());
        assertEquals(expensePage.getContent().get(0).getAmount(), actual.getContent().get(0).getAmount());
    }

    @Test
    void getUserExpenses_AnyOneFilterIsNull_Success() {

        Page<Expense> expensePage = new PageImpl<>(
                Collections.singletonList(tempExpense)
        );

        when(expenseRepository.getExpenseByUid(
                pageable, tempUserInfo.getUid(), null, List.of(tempPaymentMethodKey), Pair.of(0L, 929297696L))
        ).thenReturn(
                expensePage
        );

        mockExpenseCategoryAPIForGettingAllCategories();

        FilterRequest filterRequest = FilterRequest.builder()
                .paymentMethodKeys(List.of(tempPaymentMethodKey))
                .dateRange(Pair.of(0L, 929297696L))
                .build();


        Page<ExpenseResponse> actual = expenseService.getUserExpenses(
                pageable, filterRequest
        );

        assertEquals(expensePage.getTotalElements(), actual.getTotalElements());
        assertEquals(expensePage.getContent().get(0).getAmount(), actual.getContent().get(0).getAmount());
    }

    @Test
    void getUserExpenses_AllFilterIsNull_NoExpenseFound_Success() {

        Page<Expense> expensePage = new PageImpl<>(
                new ArrayList<>()
        );

        when(expenseRepository.getExpenseByUid(
                pageable, tempUserInfo.getUid(), null, null, null)
        ).thenReturn(
                expensePage
        );

        Page<ExpenseResponse> actual = expenseService.getUserExpenses(
                pageable, new FilterRequest()
        );

        assertEquals(0, actual.getTotalElements());
    }

    @Test
    void getUserExpenses_AllFilterIsNull_ExpenseWithDifferentUid_ExceptionThrown() {

        tempExpense.setUid("dshhjbkbhsb");
        Page<Expense> expensePage = new PageImpl<>(
                Collections.singletonList(tempExpense)
        );

        when(expenseRepository.getExpenseByUid(
                pageable, tempUserInfo.getUid(), null, null, null)
        ).thenReturn(
                expensePage
        );

        assertThatThrownBy(() -> expenseService.getUserExpenses(
                pageable, new FilterRequest()
        ))
                .isInstanceOf(ExpenseException.class)
                .hasMessage(ErrorMessageConstants.PERMISSION_DENIED);
    }


    @Test
    void getTotalExpenseAmount_Success() {

        ExpenseAmountSum expected = new ExpenseAmountSum(501726.0);

        when(expenseRepository.getTotalExpenseAmountByUid(
                tempUserInfo.getUid(), List.of(tempCategoryKey), null, null
        )).thenReturn(expected);

        ExpenseAmountSum actual = expenseService.getTotalExpenseAmount(
                new FilterRequest(List.of(tempCategoryKey), null, null)
        );

        assertEquals(expected.getTotalExpenseAmount(), actual.getTotalExpenseAmount());

    }


    @Test
    void getTotalExpenseAmountForEachCategory_Success() {

        ExpenseAmountSumAndCategoryKey expected =
                new ExpenseAmountSumAndCategoryKey(6666.0, tempCategoryKey);

        when(expenseRepository.getTotalExpenseAmountForEachCategoryByUid(
                tempUserInfo.getUid(), null, null, null
        )).thenReturn(List.of(expected));

        List<ExpenseAmountSumAndCategoryKey> actual = expenseService.getTotalExpenseAmountForEachCategory(
                new FilterRequest()
        );

        assertEquals(expected.getTotalExpenseAmount(), actual.get(0).getTotalExpenseAmount());

    }


    @Test
    void getTotalExpenseAmountForEachCategoryByKeys_Success() {

        ExpenseAmountSumAndCategoryKey expected =
                new ExpenseAmountSumAndCategoryKey(6666.0, tempCategoryKey);

        when(expenseRepository.getTotalExpenseAmountForEachCategoryByUidAndKeys(
                tempUserInfo.getUid(), List.of(tempExpense.getKey()), null, null
        )).thenReturn(List.of(expected));

        List<ExpenseAmountSumAndCategoryKey> actual = expenseService.getTotalExpenseAmountForEachCategoryByKeys(
                List.of(tempExpense.getKey()), null, null
        );

        assertEquals(expected.getTotalExpenseAmount(), actual.get(0).getTotalExpenseAmount());

    }

    @Test
    void deleteExpense_Success() {

        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(
                Optional.of(tempExpense)
        );

        doNothing().when(expenseRepository).delete(tempExpense);

        expenseService.deleteExpense(tempExpense.getKey());

        verify(expenseRepository, times(1)).delete(tempExpense);

    }

    @Test
    void deleteExpense_NoExpenseFound_ExceptionThrown() {

        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(() -> expenseService.deleteExpense(tempExpense.getKey()))
                .isInstanceOf(ExpenseException.class)
                .hasMessage(ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR);
    }

    @Test
    void deleteExpense_UidNotMatch_ExceptionThrown() {

        tempExpense.setUid("snkjsnksn");
        when(expenseRepository.findByKey(tempExpense.getKey())).thenReturn(
                Optional.of(tempExpense)
        );

        assertThatThrownBy(() -> expenseService.deleteExpense(tempExpense.getKey()))
                .isInstanceOf(ExpenseException.class)
                .hasMessage(ErrorMessageConstants.PERMISSION_DENIED);
    }


    @Test
    void deleteExpenseByCategoryKey_Success() {

        doNothing().when(expenseRepository).deleteByUidAndCategoryKey(anyString(), anyString());

        expenseService.deleteExpenseByCategoryKey(tempCategoryKey);

        verify(expenseRepository, times(1)).deleteByUidAndCategoryKey(
                tempUserInfo.getUid(), tempCategoryKey
        );

    }
}