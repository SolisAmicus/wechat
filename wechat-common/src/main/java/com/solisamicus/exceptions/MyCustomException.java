package com.solisamicus.exceptions;

import com.solisamicus.grace.result.ResponseStatusEnum;
import lombok.Getter;

@Getter
public class MyCustomException extends RuntimeException {

    private ResponseStatusEnum responseStatusEnum;

    public MyCustomException(ResponseStatusEnum responseStatusEnum) {
        super("Exception status code: " + responseStatusEnum.status() + ", message: " + responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }
}
