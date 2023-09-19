package com.rkumar0206.mymexpenseservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private String name;
    private String emailId;
    private String uid;
    private boolean isAccountVerified;
}
