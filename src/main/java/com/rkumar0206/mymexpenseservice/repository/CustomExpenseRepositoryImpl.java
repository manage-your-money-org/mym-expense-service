package com.rkumar0206.mymexpenseservice.repository;

import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSum;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSumAndCategoryKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;

import java.util.List;

@RequiredArgsConstructor
public class CustomExpenseRepositoryImpl implements CustomExpenseRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Expense> getExpenseByUid(
            Pageable pageable,
            String uid,
            List<String> categoryKeys,
            List<String> paymentMethodKeys,
            Pair<Long, Long> dateRange
    ) {

        Pair<Criteria, Criteria> dateRangeCriteria = createDateRangeCriteria(dateRange);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("uid").is(uid),
                dateRangeCriteria.getFirst(),
                dateRangeCriteria.getSecond(),
                (categoryKeys != null && !categoryKeys.isEmpty()) ? Criteria.where("categoryKey").in(categoryKeys) : new Criteria(),
                (paymentMethodKeys != null && !paymentMethodKeys.isEmpty()) ? Criteria.where("paymentMethodKeys").in(paymentMethodKeys) : new Criteria()
        );

        MatchOperation match = Aggregation.match(criteria);
        SkipOperation skip = Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize());
        LimitOperation limit = Aggregation.limit(pageable.getPageSize());
        SortOperation sort = Aggregation.sort(pageable.getSort().isEmpty() ? Sort.by(Sort.Direction.DESC, "expenseDate") : pageable.getSort());

        Aggregation aggregation = Aggregation.newAggregation(match, sort, skip, limit);

        AggregationResults<Expense> result = mongoTemplate.aggregate(aggregation, Expense.class, Expense.class);

        List<Expense> expenses = result.getMappedResults();
        long totalCount = mongoTemplate.count(Query.query(criteria), Expense.class);

        return new PageImpl<>(expenses, pageable, totalCount);
    }

//    @Override
//    public ExpenseAmountSum getTotalExpenseAmountByUid(String uid, List<String> paymentMethodKeys, Pair<Long, Long> dateRange) {
//
//        Pair<Criteria, Criteria> dateRangeCriteria = createDateRangeCriteria(dateRange);
//
//        Criteria criteria = new Criteria().andOperator(
//                Criteria.where("uid").is(uid),
//                dateRangeCriteria.getFirst(),
//                dateRangeCriteria.getSecond(),
//                (paymentMethodKeys != null && !paymentMethodKeys.isEmpty()) ? Criteria.where("paymentMethodKeys").in(paymentMethodKeys) : new Criteria()
//        );
//
//        return getTotalExpenseAmountByMatchingCriteria(criteria);
//    }


    @Override
    public ExpenseAmountSum getTotalExpenseAmountByUid(String uid, List<String> categoryKeys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange) {

        Pair<Criteria, Criteria> dateRangeCriteria = createDateRangeCriteria(dateRange);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("uid").is(uid),
                (categoryKeys != null && !categoryKeys.isEmpty()) ? Criteria.where("categoryKey").in(categoryKeys) : new Criteria(),
                dateRangeCriteria.getFirst(),
                dateRangeCriteria.getSecond(),
                (paymentMethodKeys != null && !paymentMethodKeys.isEmpty()) ? Criteria.where("paymentMethodKeys").in(paymentMethodKeys) : new Criteria()
        );

        return getTotalExpenseAmountByMatchingCriteria(criteria);
    }


    @Override
    public List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByUid(String uid, List<String> categoryKeys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange) {

        Pair<Criteria, Criteria> dateRangeCriteria = createDateRangeCriteria(dateRange);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("uid").is(uid),
                (categoryKeys != null && !categoryKeys.isEmpty()) ? Criteria.where("categoryKey").in(categoryKeys) : new Criteria(),
                dateRangeCriteria.getFirst(),
                dateRangeCriteria.getSecond(),
                (paymentMethodKeys != null && !paymentMethodKeys.isEmpty()) ? Criteria.where("paymentMethodKeys").in(paymentMethodKeys) : new Criteria()
        );

        return getTotalExpenseAmountForEachCategoryByMatchingCriteria(criteria);
    }


    @Override
    public List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByUidAndKeys(String uid, List<String> keys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange) {

        Pair<Criteria, Criteria> dateRangeCriteria = createDateRangeCriteria(dateRange);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("uid").is(uid),
                Criteria.where("key").in(keys),
                dateRangeCriteria.getFirst(),
                dateRangeCriteria.getSecond(),
                (paymentMethodKeys != null && !paymentMethodKeys.isEmpty()) ? Criteria.where("paymentMethodKeys").in(paymentMethodKeys) : new Criteria()
        );

        return getTotalExpenseAmountForEachCategoryByMatchingCriteria(criteria);
    }

    private Pair<Criteria, Criteria> createDateRangeCriteria(Pair<Long, Long> dateRange) {

        Pair<Criteria, Criteria> dateRangeCriteria = Pair.of(new Criteria(), new Criteria());

        if (dateRange != null) {

            dateRangeCriteria = Pair.of(
                    Criteria.where("expenseDate").gte(dateRange.getFirst()),
                    Criteria.where("expenseDate").lte(dateRange.getSecond())
            );
        }

        return dateRangeCriteria;
    }


    private ExpenseAmountSum getTotalExpenseAmountByMatchingCriteria(Criteria criteria) {

        AggregationOperation match = Aggregation.match(criteria);

        GroupOperation group = Aggregation.group()
                .sum("amount")
                .as("totalExpenseAmount");

        Aggregation aggregation = Aggregation.newAggregation(match, group);

        AggregationResults<ExpenseAmountSum> result = mongoTemplate.aggregate(
                aggregation, Expense.class, ExpenseAmountSum.class);

        return result.getUniqueMappedResult();
    }

    private List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByMatchingCriteria(Criteria criteria) {

        AggregationOperation match = Aggregation.match(criteria);

        GroupOperation group = Aggregation.group("categoryKey")
                .sum("amount")
                .as("totalExpenseAmount");

        AggregationOperation project = Aggregation.project()
                .and("_id").as("categoryKey")
                .and("totalExpenseAmount").as("totalExpenseAmount");

        Aggregation aggregation = Aggregation.newAggregation(match, group, project);

        AggregationResults<ExpenseAmountSumAndCategoryKey> result = mongoTemplate.aggregate(
                aggregation, Expense.class, ExpenseAmountSumAndCategoryKey.class);

        return result.getMappedResults();
    }

}
