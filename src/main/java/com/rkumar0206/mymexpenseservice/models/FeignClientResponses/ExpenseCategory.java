package com.rkumar0206.mymexpenseservice.models.FeignClientResponses;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ExpenseCategory {

    private String categoryName;
    private String categoryDescription;
    private String imageUrl;
    private Date created;
    private Date modified;
    private String uid;
    private String key;
}
