package com.hysens.hermes.common.model;

import com.hysens.hermes.common.model.enums.ChatStatusEnum;
import com.hysens.hermes.common.model.enums.CountryEnum;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.common.util.StringListConverter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Column(unique = true)
    private String phone;
    private String email;
    private ChatStatusEnum chatStatus = ChatStatusEnum.ACTIVE;
    private long partnerId;
    @OneToOne(fetch = FetchType.EAGER, targetEntity = SimpleMessage.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "last_message_id")
    private SimpleMessage lastMessage;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime createdDateTime;
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = CountryEnum.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id")
    private CountryEnum countryEnum;
    @ElementCollection(targetClass = MessengerEnum.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<MessengerEnum> messengers = new ArrayList<>();
    @Convert(converter = StringListConverter.class)
    private List<String> tags;
    private String notes;

    public Client(boolean isOnline, String name, String surname, String phone, String email, CountryEnum countryEnum) {
        this.isOnline = isOnline;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.countryEnum = countryEnum;
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

    public void addConfirmedMessenger(MessengerEnum messenger) {
        if (!this.getMessengers().contains(messenger)) {
            List<MessengerEnum> exist = new ArrayList<>();
            exist.addAll(this.getMessengers());
            exist.add(messenger);
            this.setMessengers(exist);
        }
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public SimpleMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(SimpleMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ChatStatusEnum getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(ChatStatusEnum chatStatus) {
        this.chatStatus = chatStatus;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
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

//    public String getLastMessage() {
//        return lastMessage;
//    }
//
//    public void setLastMessage(String lastMessage) {
//        this.lastMessage = lastMessage;
//    }
//
//    public LocalDateTime getLastMessageDate() {
//        return lastMessageDate;
//    }
//
//    public void setLastMessageDate(LocalDateTime lastMessageDate) {
//        this.lastMessageDate = lastMessageDate;
//    }

    public CountryEnum getCountry() {
        return countryEnum;
    }

    public void setCountry(CountryEnum countryEnum) {
        this.countryEnum = countryEnum;
    }

    public List<MessengerEnum> getMessengers() {
        return messengers;
    }

    public void setMessengers(List<MessengerEnum> messengers) {
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
