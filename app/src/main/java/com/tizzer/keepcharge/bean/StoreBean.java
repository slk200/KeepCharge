package com.tizzer.keepcharge.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class StoreBean implements Serializable {

    private int id;
    private String name;
    private double retain;
    private double income;
    private double payment;
    private int type;

    public StoreBean() {
    }

    public StoreBean(int type) {
        this.type = type;
    }

    public StoreBean(int id, String name, double retain, double income, double payment, int type) {
        this.id = id;
        this.name = name;
        this.retain = retain;
        this.income = income;
        this.payment = payment;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRetain() {
        return retain;
    }

    public void setRetain(double retain) {
        this.retain = retain;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreBean storeBean = (StoreBean) o;
        return id == storeBean.id &&
                Double.compare(storeBean.retain, retain) == 0 &&
                Double.compare(storeBean.income, income) == 0 &&
                Double.compare(storeBean.payment, payment) == 0 &&
                type == storeBean.type &&
                Objects.equals(name, storeBean.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, retain, income, payment, type);
    }

    @NonNull
    @Override
    public String toString() {
        return "StoreBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", retain=" + retain +
                ", income=" + income +
                ", payment=" + payment +
                ", type=" + type +
                '}';
    }
}
