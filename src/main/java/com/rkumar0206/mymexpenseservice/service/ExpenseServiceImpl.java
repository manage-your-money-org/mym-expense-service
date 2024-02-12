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
import com.rkumar0206.mymexpenseservice.utility.ModelMapper;
import com.rkumar0206.mymexpenseservice.utility.MymUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserContextService userContextService;
    private final ExpenseCategoryAPI expenseCategoryAPI;

    @Value("${pagination.maxPageSizeAllowed}")
    private int maxPageSizeAllowed;

    @Override
    public ExpenseResponse create(ExpenseRequest request) {

        String uid = getUserInfo().getUid();

        List<String> finalPaymentMethodKeys = new ArrayList<>();
        List<PaymentMethod> finalPaymentMethod = new ArrayList<>();

        handlePaymentMethodsAddition(request, finalPaymentMethodKeys, finalPaymentMethod, uid);

        Expense expense = new Expense(
                null,
                request.getAmount(),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                request.getExpenseDate(),
                request.getSpentOn().trim(),
                uid,
                MymUtil.createNewKey(uid),
                request.getCategoryKey(),
                finalPaymentMethodKeys
        );

        expenseRepository.save(expense);


        return ModelMapper.buildExpenseResponse(expense, getExpenseCategory(expense.getCategoryKey()), finalPaymentMethod);
    }


    @Override
    public ExpenseResponse update(ExpenseRequest request) {

        // handle paymentMethod properly: means which paymentMethod is newly added and which is removed
        String uid = getUserInfo().getUid();

        Optional<Expense> expenseDB = expenseRepository.findByKey(request.getKey());

        if (expenseDB.isEmpty()) {

            throw new ExpenseException(ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR);
        }

        if (!expenseDB.get().getUid().equals(uid)) {

            throw new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED);
        }

        List<String> finalPaymentMethodKeys = new ArrayList<>();
        List<PaymentMethod> finalPaymentMethod = new ArrayList<>();

        handlePaymentMethodsAddition(request, finalPaymentMethodKeys, finalPaymentMethod, uid);

        expenseDB.get().updateFields(request, finalPaymentMethodKeys);

        expenseRepository.save(expenseDB.get());

        return ModelMapper.buildExpenseResponse(expenseDB.get(), getExpenseCategory(expenseDB.get().getCategoryKey()), finalPaymentMethod);
    }

    private void handlePaymentMethodsAddition(
            ExpenseRequest request,
            List<String> finalPaymentMethodKeys,
            List<PaymentMethod> finalPaymentMethod,
            String uid
    ) {

        if (request.getPaymentMethodsKeys() != null && !request.getPaymentMethodsKeys().isEmpty()) {

            // get all the payment method by keys
            List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUidAndKeyIn(uid, request.getPaymentMethodsKeys());
            paymentMethods.forEach(pm -> {
                finalPaymentMethodKeys.add(pm.getKey());
                finalPaymentMethod.add(pm);
            });

        }

        if (request.getNewPaymentMethod() != null && !request.getNewPaymentMethod().isEmpty()) {

            for (String pmNewName : request.getNewPaymentMethod()) {

                Optional<PaymentMethod> paymentMethod = paymentMethodRepository.findByUidAndPaymentMethodName(
                        uid, pmNewName
                );

                // first check any payment method with same name is already there or not
                if (paymentMethod.isPresent()) {

                    // if yes, then don't add any new payment method in db and use which is already existing
                    if (finalPaymentMethod.stream()
                            .map(PaymentMethod::getPaymentMethodName)
                            .noneMatch(p -> p.equals(paymentMethod.get().getPaymentMethodName()))) {

                        finalPaymentMethodKeys.add(paymentMethod.get().getKey());
                        finalPaymentMethod.add(paymentMethod.get());
                    }

                } else {

                    // if no, then add new PaymentMethod in db and use it
                    PaymentMethod pm = new PaymentMethod(
                            null, MymUtil.createNewKey(uid), uid, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()),
                            pmNewName.trim()
                    );

                    paymentMethodRepository.save(pm);
                    finalPaymentMethodKeys.add(pm.getKey());
                    finalPaymentMethod.add(pm);
                }
            }
        }
    }

    @Override
    public ExpenseResponse getExpenseByKey(String key) {

        String uid = getUserInfo().getUid();

        Optional<Expense> expense = expenseRepository.findByKey(key);

        if (expense.isEmpty())
            throw new ExpenseException(ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR);

        if (!expense.get().getUid().equals(uid))
            throw new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED);

        List<PaymentMethod> paymentMethods = expense.get().getPaymentMethodKeys().isEmpty()
                ? new ArrayList<>()
                : paymentMethodRepository.findByUidAndKeyIn(uid, expense.get().getPaymentMethodKeys());

        ExpenseCategory expenseCategory = getExpenseCategory(expense.get().getCategoryKey());

        return ModelMapper.buildExpenseResponse(expense.get(), expenseCategory, paymentMethods);
    }

    private ExpenseCategory getExpenseCategory(String categoryKey) {

        log.info(MymUtil.createLog(
                userContextService.getCorrelationId(),
                "Sending request to expense-category-service for getting category by key"
        ));

        ResponseEntity<CustomResponse<ExpenseCategory>> categoryResponse = expenseCategoryAPI.getExpenseCategoryByKey(
                userContextService.getAuthorizationToken(),
                userContextService.getCorrelationId(),
                userContextService.getUserInfoHeaderValue(),
                categoryKey
        );

        log.info(MymUtil.createLog(
                userContextService.getCorrelationId(),
                "Response code : " + categoryResponse.getStatusCode()
        ));

        if (categoryResponse.getStatusCode() == HttpStatus.OK) {

            return Objects.requireNonNull(categoryResponse.getBody()).getBody();
        }

        return ExpenseCategory.builder().key(categoryKey).build();
    }


    @Override
    public Page<ExpenseResponse> getUserExpenses(
            Pageable pageable,
            @RequestBody FilterRequest filterRequest
    ) {

        String uid = getUserInfo().getUid();

        Page<Expense> expenses;

        if (filterRequest == null) {
            filterRequest = new FilterRequest();
        }

        if (filterRequest.getCategoryKeys() != null && filterRequest.getPaymentMethodKeys() != null && filterRequest.getDateRange() != null) {

            // query made by mongo-db
            expenses = expenseRepository.findByUidAndCategoryKeyInAndPaymentMethodKeysInAndExpenseDateBetween(
                    uid, filterRequest.getCategoryKeys(), filterRequest.getPaymentMethodKeys(), filterRequest.getDateRange().getFirst(), filterRequest.getDateRange().getSecond(), pageable
            );

        } else {

            // custom query
            expenses = expenseRepository.getExpenseByUid(
                    pageable, uid, filterRequest.getCategoryKeys(), filterRequest.getPaymentMethodKeys(), filterRequest.getDateRange()
            );
        }

        if (expenses.getTotalElements() == 0)
            return new PageImpl<>(new ArrayList<>(), pageable, 0);

        List<ExpenseResponse> expenseResponseList = convertExpensePageToExpenseResponseList(expenses, uid);

        return new PageImpl<>(expenseResponseList, pageable, expenses.getTotalElements());
    }


    /**
     * @param filterRequest (Optional)
     * @return sum of expense amount by given list of categories
     */
    @Override
    public ExpenseAmountSum getTotalExpenseAmount(FilterRequest filterRequest) {

        FilterRequest filter = filterRequest == null ? new FilterRequest() : filterRequest;

        return expenseRepository.getTotalExpenseAmountByUid(
                getUserInfo().getUid(), filter.getCategoryKeys(), filter.getPaymentMethodKeys(), filter.getDateRange()
        );
    }


    /**
     * @param filterRequest (Optional)
     * @return sum of expense of each category of a user
     */
    @Override
    public List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategory(FilterRequest filterRequest) {

        FilterRequest filter = filterRequest == null ? new FilterRequest() : filterRequest;

        return expenseRepository.getTotalExpenseAmountForEachCategoryByUid(getUserInfo().getUid(), filter.getCategoryKeys(), filter.getPaymentMethodKeys(), filter.getDateRange());
    }

    /**
     * @param keys              (Required)
     * @param paymentMethodKeys (Optional)
     * @param dateRange         (Optional)
     * @return sum of expense of categories for a given expenses
     */
    @Override
    public List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByKeys(List<String> keys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange) {

        return expenseRepository.getTotalExpenseAmountForEachCategoryByUidAndKeys(getUserInfo().getUid(), keys, paymentMethodKeys, dateRange);
    }

    @Override
    public void deleteExpense(String key) {

        String uid = getUserInfo().getUid();

        Optional<Expense> expense = expenseRepository.findByKey(key);

        if (expense.isEmpty())
            throw new ExpenseException(ErrorMessageConstants.NO_EXPENSE_FOUND_ERROR);

        if (!expense.get().getUid().equals(uid))
            throw new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED);

        expenseRepository.delete(expense.get());
    }

    @Override
    public void deleteExpenseByCategoryKey(String categoryKey) {

        String uid = getUserInfo().getUid();

        expenseRepository.deleteByUidAndCategoryKey(uid, categoryKey);
    }

    private UserInfo getUserInfo() {

        return userContextService.getUserInfo();
    }

    private List<ExpenseResponse> convertExpensePageToExpenseResponseList(Page<Expense> expenses, String uid) {

        if (!expenses.getContent().isEmpty() && !expenses.getContent().get(0).getUid().equals(uid))
            throw new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED);

        List<PaymentMethod> userPaymentMethod = paymentMethodRepository.findByUid(uid);

        List<ExpenseCategory> expenseCategoriesOfUser = getExpenseCategoriesOfUser();

        List<ExpenseResponse> expenseResponses = new ArrayList<>();

        for (Expense expense : expenses.getContent()) {

            List<PaymentMethod> paymentMethods = new ArrayList<>();

            for (String paymentMethodKey : expense.getPaymentMethodKeys()) {
                for (PaymentMethod userPayment : userPaymentMethod) {
                    if (userPayment.getKey().equals(paymentMethodKey)) {
                        paymentMethods.add(userPayment);
                        break;
                    }
                }
            }

            ExpenseCategory expenseCategory = ExpenseCategory.builder().key(expense.getCategoryKey()).build();

            for (ExpenseCategory ec : expenseCategoriesOfUser) {

                if (ec.getKey().equals(expense.getCategoryKey())) {

                    expenseCategory = ec;
                    break;
                }
            }

            expenseResponses.add(ModelMapper.buildExpenseResponse(expense, expenseCategory, paymentMethods));
        }

        return expenseResponses;
    }

    private List<ExpenseCategory> getExpenseCategoriesOfUser() {

        List<ExpenseCategory> expenseCategories = new ArrayList<>();

        int retryCount = 1;
        int pageNumber = 0;

        log.info(MymUtil.createLog(
                userContextService.getCorrelationId(),
                "Sending request to expense-category-service for getting user categories"
        ));

        while (true) {

            // get user expense categories
            ResponseEntity<CustomResponse<Page<ExpenseCategory>>> expenseCategoryOfUserResponse = expenseCategoryAPI.getExpenseCategoryOfUser(
                    userContextService.getAuthorizationToken(),
                    userContextService.getCorrelationId(),
                    userContextService.getUserInfoHeaderValue(),
                    PageRequest.of(pageNumber, maxPageSizeAllowed == 0 ? 200 : maxPageSizeAllowed)
            );

            if (expenseCategoryOfUserResponse.getStatusCode() == HttpStatus.OK) {

                expenseCategories = Objects.requireNonNull(expenseCategoryOfUserResponse.getBody()).getBody().getContent();

                int totalPages = expenseCategoryOfUserResponse.getBody().getBody().getTotalPages();

                if ((totalPages - 1) == pageNumber) {
                    break;
                } else {
                    ++pageNumber;
                }
            } else {

                if (retryCount == 3) {
                    break;
                } else {
                    ++retryCount;
                }
            }
        }

        return expenseCategories;
    }
}
