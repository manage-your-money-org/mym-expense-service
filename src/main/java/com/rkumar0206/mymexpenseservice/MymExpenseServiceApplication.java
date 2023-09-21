package com.rkumar0206.mymexpenseservice;

import com.rkumar0206.mymexpenseservice.domain.Expense;
import com.rkumar0206.mymexpenseservice.domain.PaymentMethod;
import com.rkumar0206.mymexpenseservice.repository.ExpenseRepository;
import com.rkumar0206.mymexpenseservice.repository.PaymentMethodRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class MymExpenseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MymExpenseServiceApplication.class, args);
    }

//    @Bean
//    CommandLineRunner run(ExpenseRepository expenseRepository, PaymentMethodRepository paymentMethodRepository) {
//
//        return args -> {
//
//            String uid = "0c746830dd0e4d8cb85e87fc623b359b";
//
//            List<PaymentMethod> paymentMethods = new ArrayList<>();
//
//            String key1 = UUID.randomUUID().toString();
//            String key2 = UUID.randomUUID().toString();
//            String key3 = UUID.randomUUID().toString();
//
//            paymentMethods.add(new PaymentMethod(null, key1, uid, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "HDFC"));
//            paymentMethods.add(new PaymentMethod(null, key2, uid, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "BOI"));
//            paymentMethods.add(new PaymentMethod(null, key3, uid, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), "CASH"));
//
//            paymentMethodRepository.saveAll(paymentMethods);
//
//            List<String> pm1 = Arrays.asList(key1, key2);
//            List<String> pm2 = Collections.singletonList(key3);
//
//            for (int i = 0; i < 400; i++) {
//
//                Expense expense = new Expense(
//                        null,
//                        new Random().nextDouble(0.0, 10000),
//                        new Date(System.currentTimeMillis()),
//                        new Date(System.currentTimeMillis()),
//                        System.currentTimeMillis() - new Random().nextInt(500000, 528652),
//                        UUID.randomUUID().toString(),
//                        uid,
//                        UUID.randomUUID().toString(),
//                        "f16f2219_73c195d3-d650-4665-98d3-ba73e4c901cb",
//                        i % 2 == 0 ? pm1 : pm2
//                );
//
//                expenseRepository.save(expense);
//                Thread.sleep(50);
//            }
//
//        };
//    }


//    @Bean
//    CommandLineRunner run(ExpenseRepository expenseRepository, PaymentMethodRepository paymentMethodRepository) {
//
//        return args -> {
//
//            expenseRepository.deleteAll();
//            paymentMethodRepository.deleteAll();
//        };
//    }

}
