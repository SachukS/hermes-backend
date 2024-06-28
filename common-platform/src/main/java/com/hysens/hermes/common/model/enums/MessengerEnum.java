package com.hysens.hermes.common.model.enums;

import java.util.Arrays;

public enum MessengerEnum {
    TELEGRAM("Telegram"), WHATSAPP("Whatsapp");

    private String name;

    MessengerEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MessengerEnum getByValue(String mean) {
        return Arrays.stream(MessengerEnum.values()).filter(f -> f.getName().equals(mean)).findAny().orElse(null);
    }
}
