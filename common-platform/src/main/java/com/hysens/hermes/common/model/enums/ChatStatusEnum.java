package com.hysens.hermes.common.model.enums;

import java.util.Arrays;

public enum ChatStatusEnum {
    ACTIVE("ACTIVE"), CLOSED("CLOSED");

    private String name;

    ChatStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ChatStatusEnum getByValue(String mean) {
        return Arrays.stream(ChatStatusEnum.values()).filter(f -> f.getName().equals(mean)).findAny().orElse(null);
    }
}
