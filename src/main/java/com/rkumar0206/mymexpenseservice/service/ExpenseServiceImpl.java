package com.rkumar0206.mymexpenseservice.service;

import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.exception.ExpenseException;
import com.rkumar0206.mymexpenseservice.models.UserInfo;
import com.rkumar0206.mymexpenseservice.models.request.ExpenseRequest;
import com.rkumar0206.mymexpenseservice.models.response.ExpenseResponse;
import com.rkumar0206.mymexpenseservice.repository.ExpenseRepository;
import com.rkumar0206.mymexpenseservice.repository.PaymentMethodRepository;
import com.rkumar0206.mymexpenseservice.utility.ModelMapper;
import com.rkumar0206.mymexpenseservice.utility.MymUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserContextService userContextService;

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
        return ModelMapper.buildExpenseResponse(expense, finalPaymentMethod);
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

        return ModelMapper.buildExpenseResponse(expenseDB.get(), finalPaymentMethod);
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

        return ModelMapper.buildExpenseResponse(expense.get(), paymentMethods);
    }

    @Override
    public Page<ExpenseResponse> getUserExpenses(Pageable pageable) {

        String uid = getUserInfo().getUid();

        Page<Expense> expenses = expenseRepository.findByUid(pageable, uid);

        if (expenses.getTotalElements() == 0)
            return new PageImpl<>(new ArrayList<>(), pageable, 0);

        List<ExpenseResponse> expenseResponseList = convertExpensePageToExpenseResponseList(expenses, uid);

        return new PageImpl<>(expenseResponseList, pageable, expenses.getTotalElements());
    }


    @Override
    public Page<ExpenseResponse> getUserExpenseByPaymentMethodKey(Pageable pageable, List<String> paymentMethodKeys) {

        String uid = getUserInfo().getUid();

        Page<Expense> expenses = expenseRepository.findByUidAndPaymentMethodKeysIn(uid, paymentMethodKeys, pageable);

        if (expenses.getTotalElements() == 0)
            return new PageImpl<>(new ArrayList<>(), pageable, 0);

        return new PageImpl<>(convertExpensePageToExpenseResponseList(expenses, uid), pageable, expenses.getTotalElements());
    }

    @Override
    public Page<ExpenseResponse> getExpenseBetweenStartDateAndEndDate(Pageable pageable, Long startDate, Long endDate) {

        String uid = getUserInfo().getUid();

        Page<Expense> expenses = expenseRepository.findByUidAndExpenseDateBetween(uid, startDate, endDate, pageable);

        if (expenses.getTotalElements() == 0)
            return new PageImpl<>(new ArrayList<>(), pageable, 0);


        return new PageImpl<>(convertExpensePageToExpenseResponseList(expenses, uid), pageable, expenses.getTotalElements());
    }

    @Override
    public Page<ExpenseResponse> getExpenseByCategoryKey(Pageable pageable, String categoryKey) {

        String uid = getUserInfo().getUid();

        Page<Expense> expenses = expenseRepository.findByUidAndCategoryKey(uid, categoryKey, pageable);

        if (expenses.getTotalElements() == 0)
            return new PageImpl<>(new ArrayList<>(), pageable, 0);

        return new PageImpl<>(convertExpensePageToExpenseResponseList(expenses, uid), pageable, expenses.getTotalElements());
    }

    private List<ExpenseResponse> convertExpensePageToExpenseResponseList(Page<Expense> expenses, String uid) {

        if (!expenses.getContent().isEmpty() && !expenses.getContent().get(0).getUid().equals(uid))
            throw new ExpenseException(ErrorMessageConstants.PERMISSION_DENIED);

        return expenses.getContent().stream().map(e -> {

                    List<PaymentMethod> paymentMethods = new ArrayList<>();

                    if (!e.getPaymentMethodKeys().isEmpty()) {
                        paymentMethods = paymentMethodRepository.findByUidAndKeyIn(uid, e.getPaymentMethodKeys());
                    }

                    return ModelMapper.buildExpenseResponse(e, paymentMethods);
                }
        ).toList();
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
}
