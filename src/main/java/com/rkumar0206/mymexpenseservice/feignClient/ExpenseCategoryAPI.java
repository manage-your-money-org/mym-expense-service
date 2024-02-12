package com.rkumar0206.mymexpenseservice.feignClient;


import com.rkumar0206.mymexpenseservice.constantsAndEnums.Headers;
import com.rkumar0206.mymexpenseservice.models.FeignClientResponses.ExpenseCategory;
import com.rkumar0206.mymexpenseservice.models.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "mym-expense-category-service"
)
public interface ExpenseCategoryAPI {

    @GetMapping("/mym/api/expensecategories/key")
    ResponseEntity<CustomResponse<ExpenseCategory>> getExpenseCategoryByKey(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken,
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestHeader(Headers.USER_INFO_HEADER_NAME) String userInfo,
            @RequestParam("key") String key
    );

    @GetMapping("/mym/api/expensecategories")
    ResponseEntity<CustomResponse<Page<ExpenseCategory>>> getExpenseCategoryOfUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken,
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestHeader(Headers.USER_INFO_HEADER_NAME) String userInfo,
            Pageable pageable
    );
}
