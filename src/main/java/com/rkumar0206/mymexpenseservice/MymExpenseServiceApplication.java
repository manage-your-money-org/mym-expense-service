package com.rkumar0206.mymexpenseservice;

import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.repository.ExpenseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class MymExpenseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MymExpenseServiceApplication.class, args);
    }

    //@Bean
    CommandLineRunner run(ExpenseRepository expenseRepository) {

        return args -> {

            List<PaymentMethod> paymentMethods1 = new ArrayList<>();
            List<PaymentMethod> paymentMethods2 = new ArrayList<>();

            paymentMethods1.add(new PaymentMethod(UUID.randomUUID().toString(), "HDFC"));
            paymentMethods1.add(new PaymentMethod(UUID.randomUUID().toString(), "BOI"));

            paymentMethods2.add(new PaymentMethod(UUID.randomUUID().toString(), "CASH"));

            List<Expense> expenses = new ArrayList<>();

            for (int i = 0; i < 20; i++) {

                Expense expense = new Expense(
                        null,
                        new Random().nextDouble(0.0, 10000),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        UUID.randomUUID().toString(),
                        "f16f2219eeb64edda90f661a94f6a734",
                        UUID.randomUUID().toString(),
                        "f16f2219_73c195d3-d650-4665-98d3-ba73e4c901cb",
                        i % 2 == 0 ? paymentMethods1 : paymentMethods2
                );

                expenses.add(expense);

            }

            expenseRepository.saveAll(expenses);

        };
    }

}
