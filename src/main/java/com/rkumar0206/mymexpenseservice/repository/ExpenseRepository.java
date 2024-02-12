package com.rkumar0206.mymexpenseservice.repository;

import com.rkumar0206.mymexpenseservice.domain.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends MongoRepository<Expense, String>, CustomExpenseRepository {

    Optional<Expense> findByKey(String key);

    Page<Expense> findByUidAndCategoryKeyInAndPaymentMethodKeysInAndExpenseDateBetween(
            String uid, List<String> categoryKeys, List<String> paymentMethodKeys, Long startDate, Long endDate, Pageable pageable
    );

    void deleteByUidAndCategoryKey(String uid, String key);

    //getExpenseByDateRangeAndExpenseCategoryKey
    Page<Expense> findByUidAndCategoryKeyAndExpenseDateBetween(String uid, String categoryKey, Long startDate, Long endDate, Pageable pageable);
}
