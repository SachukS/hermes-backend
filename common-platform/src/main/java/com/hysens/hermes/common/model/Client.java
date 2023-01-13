package com.hysens.hermes.common.model;

import com.hysens.hermes.common.model.enums.MessengerEnum;
import net.bytebuddy.implementation.bind.annotation.Default;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long telegramId = 0L;
    private boolean isOnline;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String lastMessage;
    private LocalDateTime lastMessageDate;
    private String country;
    private long messengers;

    public Client(boolean isOnline, String name, String surname, String phone, String email, String lastMessage, LocalDateTime lastMessageDate, String country, long messengers) {
        this.isOnline = isOnline;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
        this.country = country;
        this.messengers = messengers;
    }

    public Client(String phone) {
        this.phone = phone;
    }

    public Client(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Client() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(long telegramId) {
        this.telegramId = telegramId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(LocalDateTime lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getMessengers() {
        return messengers;
    }

    public void setMessengers(long messengers) {
        this.messengers = messengers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(name, client.name) && Objects.equals(surname, client.surname) && Objects.equals(phone, client.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, phone);
    }
}
