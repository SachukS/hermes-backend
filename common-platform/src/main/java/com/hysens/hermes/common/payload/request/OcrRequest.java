package com.hysens.hermes.common.payload.request;

public class OcrRequest {
    private long userId;
    private byte[] imageEncoded;
    private String type;

    public OcrRequest(long userId, byte[] imageEncoded, String type) {
        this.userId = userId;
        this.imageEncoded = imageEncoded;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public byte[] getImageEncoded() {
        return imageEncoded;
    }

    public void setImageEncoded(byte[] imageEncoded) {
        this.imageEncoded = imageEncoded;
    }
}
