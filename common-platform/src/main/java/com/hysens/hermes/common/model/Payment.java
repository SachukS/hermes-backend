package com.hysens.hermes.common.model;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String invoiceNumber;
    private long currencyTypeId;
    private long amount;
    private long partnerId;
    private long taxValue;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invoiceDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invoiceFromDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invoiceToDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyDate;

    public Payment() {
    }

    public Payment(String invoiceNumber, long currencyTypeId, long amount, long partnerId,
                   long taxValue, Date invoiceDate, Date invoiceFromDate,
                   Date invoiceToDate, Date createdDate, Date modifyDate) {
        this.invoiceNumber = invoiceNumber;
        this.currencyTypeId = currencyTypeId;
        this.amount = amount;
        this.partnerId = partnerId;
        this.taxValue = taxValue;
        this.invoiceDate = invoiceDate;
        this.invoiceFromDate = invoiceFromDate;
        this.invoiceToDate = invoiceToDate;
        this.createdDate = createdDate;
        this.modifyDate = modifyDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public long getCurrencyTypeId() {
        return currencyTypeId;
    }

    public void setCurrencyTypeId(long currencyTypeId) {
        this.currencyTypeId = currencyTypeId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public long getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(long taxValue) {
        this.taxValue = taxValue;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getInvoiceFromDate() {
        return invoiceFromDate;
    }

    public void setInvoiceFromDate(Date invoiceFromDate) {
        this.invoiceFromDate = invoiceFromDate;
    }

    public Date getInvoiceToDate() {
        return invoiceToDate;
    }

    public void setInvoiceToDate(Date invoiceToDate) {
        this.invoiceToDate = invoiceToDate;
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
