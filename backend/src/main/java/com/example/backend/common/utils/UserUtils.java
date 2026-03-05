package com.example.backend.common.utils;

import java.util.UUID;

public class UserUtils {

    public static String generateUniqueUsername() {
        String uuid = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
        return "user_" + uuid;
    }


    public static String generateUUIDToken() {
        return UUID.randomUUID().toString();
    }

}
