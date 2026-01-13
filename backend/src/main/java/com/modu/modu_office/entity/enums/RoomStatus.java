package com.modu.modu_office.entity.enums;

public enum RoomStatus {
    AVAILABLE("available"),
    INACTIVE("inactive");

    private final String dbValue;

    RoomStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
