package com.solisamicus.enums;

public enum Black {
    NO(0, "否"),
    YES(1, "是");

    public final Integer type;
    public final String value;

    Black(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
