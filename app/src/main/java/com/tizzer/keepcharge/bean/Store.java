package com.tizzer.keepcharge.bean;

import java.io.Serializable;

public class Store implements Serializable {

    private int id;
    private String name;
    private double income;
    private double payment;
    private int type;

    public Store() {
    }

    public Store(int type) {
        this.type = type;
    }

    public Store(int id, String name, double income, double payment, int type) {
        this.id = id;
        this.name = name;
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
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", income=" + income +
                ", payment=" + payment +
                ", type=" + type +
                '}';
    }
}
