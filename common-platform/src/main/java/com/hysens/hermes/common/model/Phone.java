package com.hysens.hermes.common.model;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "phone")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String phoneNumber;
    private long lastMessageId;
    private long messengerId;
    private long phoneStatusId;
    private long userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyDate;

    public Phone() {
    }

    public Phone(String phoneNumber, long lastMessageId, long messengerId,
                 long phoneStatusId, long userId, Date createdDate, Date modifyDate) {
        this.phoneNumber = phoneNumber;
        this.lastMessageId = lastMessageId;
        this.messengerId = messengerId;
        this.phoneStatusId = phoneStatusId;
        this.userId = userId;
        this.createdDate = createdDate;
        this.modifyDate = modifyDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public long getMessengerId() {
        return messengerId;
    }

    public void setMessengerId(long messengerId) {
        this.messengerId = messengerId;
    }

    public long getPhoneStatusId() {
        return phoneStatusId;
    }

    public void setPhoneStatusId(long phoneStatusId) {
        this.phoneStatusId = phoneStatusId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}
