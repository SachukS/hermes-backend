package com.hysens.hermes.common.payload.response;

public class OcrResult {
    private long userId;
    private String recognizedText;

    public OcrResult() {
    }

    public OcrResult(long userId, String recognizedText) {
        this.userId = userId;
        this.recognizedText = recognizedText;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getRecognizedText() {
        return recognizedText;
    }

    public void setRecognizedText(String recognizedText) {
        this.recognizedText = recognizedText;
    }
}
