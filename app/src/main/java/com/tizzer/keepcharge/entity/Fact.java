package com.tizzer.keepcharge.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "tb_fact")
public class Fact implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(canBeNull = false)
    private double income;

    @DatabaseField(canBeNull = false)
    private double payment;

    public Fact() {
    }

    public Fact(Integer id, double income, double payment) {
        this.id = id;
        this.income = income;
        this.payment = payment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Fact{" +
                "id=" + id +
                ", income=" + income +
                ", payment=" + payment +
                '}';
    }
}