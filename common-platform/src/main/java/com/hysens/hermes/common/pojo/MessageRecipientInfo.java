package com.hysens.hermes.common.pojo;

public class MessageRecipientInfo {
    boolean isUserExist;
    boolean isChatWithUserExist;
    boolean isMessageSended;
    String userId;

    public MessageRecipientInfo() {
        isUserExist = false;
        isMessageSended = false;
        isChatWithUserExist = false;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isUserExist() {
        return isUserExist;
    }

    public void setUserExist(boolean userExist) {
        isUserExist = userExist;
    }

    public boolean isChatWithUserExist() {
        return isChatWithUserExist;
    }

    public void setChatWithUserExist(boolean chatWithUserExist) {
        isChatWithUserExist = chatWithUserExist;
    }

    public boolean isMessageSended() {
        return isMessageSended;
    }

    public void setMessageSended(boolean messageSended) {
        isMessageSended = messageSended;
    }

    @Override
    public String toString() {
        return "MessageRecipientInfo{" +
                "isUserExist=" + isUserExist +
                ", isChatWithUserExist=" + isChatWithUserExist +
                ", isMessageSended=" + isMessageSended +
                ", userId='" + userId + '\'' +
                '}';
    }
}
