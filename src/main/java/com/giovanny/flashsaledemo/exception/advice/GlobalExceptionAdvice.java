package com.giovanny.flashsaledemo.exception.advice;

import com.giovanny.flashsaledemo.common.MyResponse;
import com.giovanny.flashsaledemo.exception.BaseRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * @author YangJun
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = {
            BindException.class,
            MethodArgumentNotValidException.class
    })
    public MyResponse handle(BindException e) {
        log.error("check system error :", e);
        StringBuilder stringBuilder = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        fieldErrors.forEach(
                fieldError -> {
                    stringBuilder
                            .append(fieldError.getField())
                            .append(":")
                            .append(fieldError.getDefaultMessage())
                            .append(System.lineSeparator());
                }
        );

        return MyResponse.failed(HttpStatus.BAD_REQUEST.value(), stringBuilder.toString());
    }


    @ExceptionHandler(value = ConstraintViolationException.class)
    public MyResponse handle(ConstraintViolationException e) {
        log.error("check system error :", e);
        StringBuilder stringBuilder = new StringBuilder();

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        constraintViolations.forEach(
                constraintViolation -> {
                    stringBuilder
                            .append(constraintViolation.getInvalidValue())
                            .append(":")
                            .append(constraintViolation.getMessage())
                            .append(System.lineSeparator());
                }
        );
        return MyResponse.failed(HttpStatus.BAD_REQUEST.value(), stringBuilder.toString());
    }

    @ExceptionHandler(value = BaseRuntimeException.class)
    public MyResponse handle(BaseRuntimeException e) {
        log.error("check system error :", e);
        return MyResponse.failed(e.getResultCode().getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public MyResponse handle(Exception e) {
        log.error("check system error :", e);
        return MyResponse.failed(e.getMessage());
    }
}
