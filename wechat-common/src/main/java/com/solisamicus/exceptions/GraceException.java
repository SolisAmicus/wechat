package com.solisamicus.exceptions;

import com.solisamicus.grace.result.ResponseStatusEnum;

public class GraceException {
    public static void display(ResponseStatusEnum statusEnum) {
        throw new MyCustomException(statusEnum);
    }
}
