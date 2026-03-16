package com.example.backend.exception;

import com.example.backend.common.enums.User.UserErrorCode;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private final UserErrorCode errorCode;

    public UserException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
