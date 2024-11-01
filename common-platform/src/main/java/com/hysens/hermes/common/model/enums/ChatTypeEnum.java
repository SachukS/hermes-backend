package com.hysens.hermes.common.model.enums;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_type_enum")
public class ChatTypeEnum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;

    public ChatTypeEnum() {
    }

    public ChatTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId(){
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
