package com.hysens.hermes.common.model;

import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class SimpleMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean fromMe;
    private String receiverPhone;
    private String senderPhone;
    private long clientId;
    @Enumerated(EnumType.STRING)
    private MessengerEnum messenger;
    @Column(length = 4096)
    private String message;
    private String messageSpecId;
    @Enumerated(EnumType.STRING)
    private MessageStatusEnum messageStatus;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime createdDate;

    public SimpleMessage() {
    }

    public SimpleMessage(long id) {
        this.id = id;
    }

    public SimpleMessage(MessengerEnum messenger, String message) {
        this.messenger = messenger;
        this.message = message;
    }
    public SimpleMessage(boolean fromMe, String message) {
        this.fromMe = fromMe;
        this.message = message;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getMessageSpecId() {
        return messageSpecId;
    }

    public void setMessageSpecId(String messageSpecId) {
        this.messageSpecId = messageSpecId;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public MessageStatusEnum getMessageStatus() {
        return messageStatus;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public void setMessageStatus(MessageStatusEnum messageStatus) {
        this.messageStatus = messageStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public MessengerEnum getMessenger() {
        return messenger;
    }

    public void setMessenger(MessengerEnum messenger) {
        this.messenger = messenger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleMessage that = (SimpleMessage) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
