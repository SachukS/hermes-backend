package com.hysens.hermes.common.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private long userTypeId;
    private String nickname;
    private long userStatusTypeId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyDate;

    public User () {

    }

    public User(String name, long userTypeId, String nickname, long userStatusTypeId, Date createdDate, Date modifyDate) {
        this.name = name;
        this.userTypeId = userTypeId;
        this.nickname = nickname;
        this.userStatusTypeId = userStatusTypeId;
        this.createdDate = createdDate;
        this.modifyDate = modifyDate;
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


    public long getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(long userTypeId) {
        this.userTypeId = userTypeId;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public long getUserStatusTypeId() {
        return userStatusTypeId;
    }

    public void setUserStatusTypeId(long userStatusTypeId) {
        this.userStatusTypeId = userStatusTypeId;
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
