package com.hysens.hermes.common.model.enums;

import javax.persistence.*;

@Entity
@Table(name = "country_enum")
public class CountryEnum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String code;

    public CountryEnum() {
    }

    public CountryEnum(String name, String code) {
        this.name = name;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String description) {
        this.code = description;
    }

}
