package com.tizzer.keepcharge.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class BillBean implements Serializable {

    private int id;
    private double money;
    private String time;
    private String note;
    private Boolean type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillBean billBean = (BillBean) o;
        return id == billBean.id &&
                Double.compare(billBean.money, money) == 0 &&
                Objects.equals(time, billBean.time) &&
                Objects.equals(note, billBean.note) &&
                Objects.equals(type, billBean.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, money, time, note, type);
    }

    @NonNull
    @Override
    public String toString() {
        return "BillBean{" +
                "id=" + id +
                ", money=" + money +
                ", time='" + time + '\'' +
                ", note='" + note + '\'' +
                ", type=" + type +
                '}';
    }
}
