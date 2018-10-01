package com.tizzer.keepcharge.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "tb_bill")
public class Bill {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private int sid;

    @DatabaseField(canBeNull = false)
    private double money;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE, format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @DatabaseField(canBeNull = false)
    private Boolean type;

    @DatabaseField(canBeNull = false)
    private String note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", sid=" + sid +
                ", money=" + money +
                ", date=" + date +
                ", type=" + type +
                ", note='" + note + '\'' +
                '}';
    }
}
