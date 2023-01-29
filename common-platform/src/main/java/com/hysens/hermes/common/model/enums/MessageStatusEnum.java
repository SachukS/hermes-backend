package com.hysens.hermes.common.model.enums;

import java.util.Arrays;

public enum MessageStatusEnum {
    SENT("SENT"), READ("READ"), UNREAD("UNREAD");

    private String name;

    MessageStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MessageStatusEnum getByValue(String mean) {
        return Arrays.stream(MessageStatusEnum.values()).filter(f -> f.getName().equals(mean)).findAny().orElse(null);
    }
}
