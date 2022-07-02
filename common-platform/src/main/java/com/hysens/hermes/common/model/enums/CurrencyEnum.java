package com.hysens.hermes.common.model.enums;

import javax.persistence.*;

@Entity
@Table(name = "currency_enum")
public class CurrencyEnum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String shortName;

    public CurrencyEnum() {
    }

    public CurrencyEnum(String shortName) {
        this.shortName = shortName;
    }

    public long getId(){
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
