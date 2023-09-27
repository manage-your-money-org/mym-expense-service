package com.rkumar0206.mymexpenseservice.controller;

import com.rkumar0206.mymexpenseservice.constantsAndEnums.Constants;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.exception.ExpenseException;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSum;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSumAndCategoryKey;
import com.rkumar0206.mymexpenseservice.models.request.ExpenseRequest;
import com.rkumar0206.mymexpenseservice.models.request.FilterRequest;
import com.rkumar0206.mymexpenseservice.models.response.CustomResponse;
import com.rkumar0206.mymexpenseservice.models.response.ExpenseResponse;
import com.rkumar0206.mymexpenseservice.service.ExpenseService;
import com.rkumar0206.mymexpenseservice.utility.ModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private Expense tempExpense;

    private Pageable pageable;


    @BeforeEach
    void setUp() {

        tempExpense = new Expense(
                "sjcbshbsbjhsgvshvhgsvg",
                50000.0,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                System.currentTimeMillis(),
                "somthing amazing",
                "nsnksnkjnksn",
                "asjcbakbababjhajh",
                "zckjsbbcbsjbscjh",
                Arrays.asList("sckjsbhsbjhsscakjbb", "acjbahjbajhbajh")
        );

        pageable = PageRequest.of(0, 200);
    }

    @Test
    void createExpense_Success() {

        ExpenseRequest expenseRequest = new ExpenseRequest(
                50000.0,
                tempExpense.getSpentOn(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),
                List.of("abkajbabkabk"),
                null,
                null
        );

        when(expenseService.create(expenseRequest)).thenReturn(ModelMapper.buildExpenseResponse(tempExpense, new ArrayList<>()));

        ResponseEntity<CustomResponse<ExpenseResponse>> actual =
                expenseController.createExpense(UUID.randomUUID().toString(), expenseRequest);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.CREATED.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
    }

    @Test
    void createExpense_RequestNotValid_BAD_REQUEST() {

        ExpenseRequest expenseRequest = new ExpenseRequest(
                null,
                tempExpense.getSpentOn(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),
                List.of("abkajbabkabk"),
                null,
                null
        );

        ResponseEntity<CustomResponse<ExpenseResponse>> actual =
                expenseController.createExpense(UUID.randomUUID().toString(), expenseRequest);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_BODY_NOT_VALID));
    }


    @Test
    void updateExpense_Success() {

        ExpenseRequest expenseRequest = new ExpenseRequest(
                10000.0,
                tempExpense.getSpentOn(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),
                List.of("abkajbabkabk"),
                null,
                UUID.randomUUID().toString()
        );

        tempExpense.setAmount(10000.0);
        when(expenseService.update(expenseRequest)).thenReturn(ModelMapper.buildExpenseResponse(
                tempExpense, null
        ));

        ResponseEntity<CustomResponse<ExpenseResponse>> actual =
                expenseController.updateExpense(UUID.randomUUID().toString(), expenseRequest);


        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertEquals(actual.getBody().getMessage(), Constants.SUCCESS);
    }

    @Test
    void updateExpense_RequestNotValid_BAD_REQUEST() {

        ExpenseRequest expenseRequest = new ExpenseRequest(
                10000.0,
                tempExpense.getSpentOn(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),
                List.of("abkajbabkabk"),
                null,
                null
        );

        ResponseEntity<CustomResponse<ExpenseResponse>> actual =
                expenseController.updateExpense(UUID.randomUUID().toString(), expenseRequest);


        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_BODY_NOT_VALID));
    }

    @Test
    void updateExpense_PermissionDenied_FORBIDDEN() {

        ExpenseRequest expenseRequest = new ExpenseRequest(
                10000.0,
                tempExpense.getSpentOn(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),
                List.of("abkajbabkabk"),
                null,
                UUID.randomUUID().toString()
        );

        when(expenseService.update(expenseRequest)).thenThrow(
                new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED)
        );

        ResponseEntity<CustomResponse<ExpenseResponse>> actual =
                expenseController.updateExpense(UUID.randomUUID().toString(), expenseRequest);


        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());
        assertNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.PERMISSION_DENIED));
    }

    @Test
    void updateExpense_NoExpenseFound_NO_CONTENT() {

        ExpenseRequest expenseRequest = new ExpenseRequest(
                10000.0,
                tempExpense.getSpentOn(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),
                List.of("abkajbabkabk"),
                null,
                UUID.randomUUID().toString()
        );

        when(expenseService.update(expenseRequest)).thenThrow(
                new ExpenseException(ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR)
        );

        ResponseEntity<CustomResponse<ExpenseResponse>> actual =
                expenseController.updateExpense(UUID.randomUUID().toString(), expenseRequest);


        assertEquals(HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()), actual.getStatusCode());
        assertNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR));
    }


    @Test
    void getAllExpenseByUid_Success() {

        when(expenseService.getUserExpenses(any(), any()))
                .thenReturn(new PageImpl<>(List.of(ModelMapper.buildExpenseResponse(tempExpense, null)), pageable, 1));

        ResponseEntity<CustomResponse<Page<ExpenseResponse>>> actual = expenseController.getAllExpenseByUid(
                pageable, UUID.randomUUID().toString(), new FilterRequest()
        );

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(Constants.SUCCESS));
    }

    @Test
    void getAllExpenseByUid_DateRangeIsValid_SUCCESS() {

        when(expenseService.getUserExpenses(any(), any()))
                .thenReturn(new PageImpl<>(List.of(ModelMapper.buildExpenseResponse(tempExpense, null)), pageable, 1));

        ResponseEntity<CustomResponse<Page<ExpenseResponse>>> actual = expenseController.getAllExpenseByUid(
                pageable, UUID.randomUUID().toString(),
                FilterRequest.builder().dateRange(Pair.of(1798378L, 8257145641L)).build() // must be 2 items in dateRange list
        );

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(Constants.SUCCESS));
    }


    @Test
    void getAllExpenseByUid_MaxSizeNotValid_BAD_REQUEST() {

        pageable = PageRequest.of(0, 300);

        ResponseEntity<CustomResponse<Page<ExpenseResponse>>> actual = expenseController.getAllExpenseByUid(
                pageable, UUID.randomUUID().toString(), new FilterRequest()
        );

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.MAX_PAGE_SIZE_ERROR.substring(0, ErrorMessageConstants.MAX_PAGE_SIZE_ERROR.length() - 3)));
    }


    @Test
    void getExpenseByKey_Success() {

        when(expenseService.getExpenseByKey(anyString())).thenReturn(
                ModelMapper.buildExpenseResponse(tempExpense, null)
        );

        ResponseEntity<CustomResponse<ExpenseResponse>> actual = expenseController.getExpenseByKey(
                UUID.randomUUID().toString(), tempExpense.getKey()
        );

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(Constants.SUCCESS));
    }

    @Test
    void getExpenseByKey_RequestNotValid_BAD_REQUEST() {

        ResponseEntity<CustomResponse<ExpenseResponse>> actual = expenseController.getExpenseByKey(
                UUID.randomUUID().toString(), null
        );

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID));
    }

    @Test
    void getExpenseAmountSum_Success() {

        when(expenseService.getTotalExpenseAmount(any())).thenReturn(
                new ExpenseAmountSum(5028.0)
        );

        ResponseEntity<CustomResponse<ExpenseAmountSum>> actual = expenseController.getExpenseAmountSum("dscnksbksbkcs", new FilterRequest());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(Constants.SUCCESS));

    }

    @Test
    void getExpenseAmountSum_SomeExceptionOccurred_AppropriateResponse() {

        when(expenseService.getTotalExpenseAmount(any())).thenThrow(
                new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED)
        );

        ResponseEntity<CustomResponse<ExpenseAmountSum>> actual = expenseController.getExpenseAmountSum("dscnksbksbkcs", new FilterRequest());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());

    }


    @Test
    void getExpenseAmountForEachCategory_Success() {

        when(expenseService.getTotalExpenseAmountForEachCategory(any()))
                .thenReturn(List.of(new ExpenseAmountSumAndCategoryKey(5635.0, tempExpense.getCategoryKey())));

        ResponseEntity<CustomResponse<List<ExpenseAmountSumAndCategoryKey>>> actual = expenseController.getExpenseAmountForEachCategory("sdjskbvsk", new FilterRequest());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(Constants.SUCCESS));

    }

    @Test
    void getExpenseAmountForEachCategory_SomeExceptionOccurred_AppropriateResponse() {

        when(expenseService.getTotalExpenseAmountForEachCategory(any()))
                .thenThrow(new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED));

        ResponseEntity<CustomResponse<List<ExpenseAmountSumAndCategoryKey>>> actual = expenseController.getExpenseAmountForEachCategory("sdjskbvsk", new FilterRequest());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());

    }


    @Test
    void getExpenseAmountForEachCategoryByExpenseKeys_Success() {


        when(expenseService.getTotalExpenseAmountForEachCategoryByKeys(
                List.of(tempExpense.getKey()), null, null
        )).thenReturn(List.of(new ExpenseAmountSumAndCategoryKey(5635.0, tempExpense.getCategoryKey())));

        ResponseEntity<CustomResponse<List<ExpenseAmountSumAndCategoryKey>>> actual = expenseController.getExpenseAmountForEachCategoryByExpenseKeys(
                "bckabkbakba", List.of(tempExpense.getKey()), null, null
        );

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(Constants.SUCCESS));

    }

    @Test
    void getExpenseAmountForEachCategoryByExpenseKeys_ExpenseKeysListIsNull_BAD_REQUEST() {

        ResponseEntity<CustomResponse<List<ExpenseAmountSumAndCategoryKey>>> actual = expenseController.getExpenseAmountForEachCategoryByExpenseKeys(
                "bckabkbakba", null, null, null
        );

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID));
    }

    @Test
    void getExpenseAmountForEachCategoryByExpenseKeys_DateRangeNotNullAndIsValid_SUCCESS() {

        when(expenseService.getTotalExpenseAmountForEachCategoryByKeys(
                any(), any(), any()
        )).thenReturn(List.of(new ExpenseAmountSumAndCategoryKey(5635.0, tempExpense.getCategoryKey())));


        ResponseEntity<CustomResponse<List<ExpenseAmountSumAndCategoryKey>>> actual = expenseController.getExpenseAmountForEachCategoryByExpenseKeys(
                "bckabkbakba", List.of(tempExpense.getKey()), null, List.of(0L, 297696L)
        );

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody());
        assertThat(actual.getBody().getMessage(), containsString(Constants.SUCCESS));
    }

    @Test
    void getExpenseAmountForEachCategoryByExpenseKeys_DateRangeNotNullAndIsNotValid_BAD_REQUEST() {

        ResponseEntity<CustomResponse<List<ExpenseAmountSumAndCategoryKey>>> actual = expenseController.getExpenseAmountForEachCategoryByExpenseKeys(
                "bckabkbakba", List.of(tempExpense.getKey()), null, List.of(0L)
        );

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID));
    }


    @Test
    void deleteExpenseByExpenseKey_Success() {

        doNothing().when(expenseService).deleteExpense(anyString());

        ResponseEntity<CustomResponse<String>> actual = expenseController.deleteExpenseByExpenseKey("dsjnkjankan", tempExpense.getKey());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()), actual.getStatusCode());
    }

    @Test
    void deleteExpenseByExpenseKey_KeyNotValid_Success() {

        ResponseEntity<CustomResponse<String>> actual = expenseController.deleteExpenseByExpenseKey("dsjnkjankan", "");

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID));

    }

    @Test
    void deleteExpenseByExpenseKey_SomeExceptionOccurred_AppropriateResponseCode() {

        doThrow(new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED))
                .when(expenseService).deleteExpense(anyString());

        ResponseEntity<CustomResponse<String>> actual = expenseController.deleteExpenseByExpenseKey("dsjnkjankan", "aasffaf");

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());

    }


    @Test
    void deleteExpenseByCategoryKey_Success() {

        doNothing().when(expenseService).deleteExpenseByCategoryKey(anyString());

        ResponseEntity<CustomResponse<String>> actual = expenseController.deleteExpenseByCategoryKey("dsnakjnak", "askjcbaacbkj");

        assertEquals(HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()), actual.getStatusCode());
    }

    @Test
    void deleteExpenseByCategoryKey_SomeExceptionOccurred_AppropriateResponseCode() {

        doThrow(new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED))
                .when(expenseService).deleteExpenseByCategoryKey(anyString());

        ResponseEntity<CustomResponse<String>> actual = expenseController.deleteExpenseByCategoryKey("dsnakjnak", "252352525");

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());
    }

    @Test
    void deleteExpenseByCategoryKey_KeyNotValid_BAD_REQUEST() {

        ResponseEntity<CustomResponse<String>> actual = expenseController.deleteExpenseByCategoryKey("dsnakjnak", "");

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.REQUEST_PARAM_NOT_VALID));

    }


}