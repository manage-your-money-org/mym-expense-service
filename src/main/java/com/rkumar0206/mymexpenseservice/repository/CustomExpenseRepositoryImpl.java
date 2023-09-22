package com.rkumar0206.mymexpenseservice.repository;

import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSum;
import com.rkumar0206.mymexpenseservice.models.data.ExpenseAmountSumAndCategoryKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.util.Pair;

import java.util.List;

@RequiredArgsConstructor
public class CustomExpenseRepositoryImpl implements CustomExpenseRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public ExpenseAmountSum getTotalExpenseAmountByUid(String uid, List<String> paymentMethodKeys, Pair<Long, Long> dateRange) {

        Pair<Criteria, Criteria> dateRangeCriteria = createDateRangeCriteria(dateRange);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("uid").is(uid),
                dateRangeCriteria.getFirst(),
                dateRangeCriteria.getSecond(),
                (paymentMethodKeys != null && !paymentMethodKeys.isEmpty()) ? Criteria.where("paymentMethodKeys").in(paymentMethodKeys) : new Criteria()
        );

        return getTotalExpenseAmountByMatchingCriteria(criteria);
    }


    @Override
    public ExpenseAmountSum getTotalExpenseByUidAndCategoryKeys(String uid, List<String> categoryKeys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange) {

        Pair<Criteria, Criteria> dateRangeCriteria = createDateRangeCriteria(dateRange);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("uid").is(uid),
                Criteria.where("categoryKey").in(categoryKeys),
                dateRangeCriteria.getFirst(),
                dateRangeCriteria.getSecond(),
                (paymentMethodKeys != null && !paymentMethodKeys.isEmpty()) ? Criteria.where("paymentMethodKeys").in(paymentMethodKeys) : new Criteria()
        );

        return getTotalExpenseAmountByMatchingCriteria(criteria);
    }

    @Override
    public List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByUid(String uid, List<String> paymentMethodKeys, Pair<Long, Long> dateRange) {

        Pair<Criteria, Criteria> dateRangeCriteria = createDateRangeCriteria(dateRange);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("uid").is(uid),
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

    @Override
    public List<ExpenseAmountSumAndCategoryKey> getTotalExpenseAmountForEachCategoryByUidAndCategoryKeys(String uid, List<String> categoryKeys, List<String> paymentMethodKeys, Pair<Long, Long> dateRange) {

        Pair<Criteria, Criteria> dateRangeCriteria = createDateRangeCriteria(dateRange);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("uid").is(uid),
                Criteria.where("categoryKey").in(categoryKeys),
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
