package ru.spbstu.entity;

public enum PayType {
    CASH("CASH"),
    NON_CASH("NON_CASH");

    private String value;

    PayType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
