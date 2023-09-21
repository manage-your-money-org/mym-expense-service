package com.rkumar0206.mymexpenseservice.repository;

import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends MongoRepository<Expense, String> {

    Optional<Expense> findByKey(String key);

    Page<Expense> findByUid(Pageable pageable, String uid);

    Page<Expense> findByUidAndPaymentMethodKeysIn(String uid, List<String> paymentMethodKeys, Pageable pageable);

    Page<Expense> findByUidAndExpenseDateBetween(String uid, Long startDate, Long endDate, Pageable pageable);

    Page<Expense> findByUidAndCategoryKey(String uid, String categoryKey, Pageable pageable);

    void deleteByUidAndCategoryKey(String uid, String key);
}
