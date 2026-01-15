package com.modu.office.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LogAction {
    CREATE("create"),
    UPDATE("update"),
    CANCEL("cancel");

    private final String value;

    LogAction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
