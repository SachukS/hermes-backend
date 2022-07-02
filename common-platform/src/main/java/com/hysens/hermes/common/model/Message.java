package com.hysens.hermes.common.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long senderPhoneId;
    private long receiverPhoneId;
    private long chatTypeId;
    private long partnerId;
    private long messengerTypeId;
    private long statusTypeId;
    private String message;
    private String mediaFile;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyDate;

    public Message() {
    }

    public Message(long senderPhoneId, long receiverPhoneId, long chatTypeId, long partnerId, long messengerTypeId,
                   long statusTypeId, String message, String mediaFile, Date createdDate, Date modifyDate) {
        this.senderPhoneId = senderPhoneId;
        this.receiverPhoneId = receiverPhoneId;
        this.chatTypeId = chatTypeId;
        this.partnerId = partnerId;
        this.messengerTypeId = messengerTypeId;
        this.statusTypeId = statusTypeId;
        this.message = message;
        this.mediaFile = mediaFile;
        this.createdDate = createdDate;
        this.modifyDate = modifyDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSenderPhoneId() {
        return senderPhoneId;
    }

    public void setSenderPhoneId(long senderPhoneId) {
        this.senderPhoneId = senderPhoneId;
    }

    public long getReceiverPhoneId() {
        return receiverPhoneId;
    }

    public void setReceiverPhoneId(long receiverPhoneId) {
        this.receiverPhoneId = receiverPhoneId;
    }

    public long getChatTypeId() {
        return chatTypeId;
    }

    public void setChatTypeId(long chatTypeId) {
        this.chatTypeId = chatTypeId;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public long getMessengerTypeId() {
        return messengerTypeId;
    }

    public void setMessengerTypeId(long messengerTypeId) {
        this.messengerTypeId = messengerTypeId;
    }

    public long getStatusTypeId() {
        return statusTypeId;
    }

    public void setStatusTypeId(long statusTypeId) {
        this.statusTypeId = statusTypeId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(String mediaFile) {
        this.mediaFile = mediaFile;
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
