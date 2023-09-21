package com.rkumar0206.mymexpenseservice.utility;

import java.util.UUID;

public class MymUtil {

    public static boolean isValid(String str) {

        return str != null && !str.trim().isEmpty();
    }

    public static String createNewKey(String uid) {

        return uid.substring(0, 8) + "_" + UUID.randomUUID();
    }
}
