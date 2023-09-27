package com.rkumar0206.mymexpenseservice.models.request;

import lombok.*;
import org.springframework.data.util.Pair;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FilterRequest {

    List<String> categoryKeys;
    List<String> paymentMethodKeys;
    Pair<Long, Long> dateRange;
}
