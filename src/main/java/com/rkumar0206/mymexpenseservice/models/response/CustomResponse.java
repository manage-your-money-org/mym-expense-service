package com.rkumar0206.mymexpenseservice.models.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CustomResponse<T> {

    private int status;
    private String message;
    private T body;
}
