package com.hysens.hermes.telegram.config;

import java.util.concurrent.CountDownLatch;

public class CommunicateMethod {

    public final CountDownLatch resultReady;

    public Object result;

    public CommunicateMethod() {
        this.resultReady = new CountDownLatch(1);
    }

    public void setResult(Object result) {
        this.result = result;
        resultReady.countDown();
    }

    public Object getResult() throws InterruptedException {
        resultReady.await();
        return result;
    }
}
