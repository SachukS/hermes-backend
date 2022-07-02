package com.hysens.hermes.common.model.enums;

import javax.persistence.*;

@Entity
@Table(name = "message_status_type_enum")
public class MessageStatusTypeEnum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;

    public MessageStatusTypeEnum() {
    }

    public MessageStatusTypeEnum(String name, String description) {
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
