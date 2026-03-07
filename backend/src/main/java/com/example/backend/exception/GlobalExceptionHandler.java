package com.example.backend.exception;


import com.example.backend.common.enums.User.UserErrorCode;
import com.example.backend.dto.API.AType;
import com.example.backend.dto.API.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<AType> handleUserException(UserException ex) {

        UserErrorCode code = ex.getErrorCode();

        AType error = ErrorType.builder()
                .code(code.getCode())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.valueOf(code.getCode()));
    }
}
