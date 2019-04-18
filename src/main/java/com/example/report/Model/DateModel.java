package com.example.report.Model;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created by Muiduzzaman Lipu on 17-Feb-19.
 */
public class DateModel {

    @DateTimeFormat( pattern="yyyy-MM-dd")
    private String date;
    private int colNumber;
    private boolean friday;

    public DateModel() {
    }

    public DateModel(String date, int colNumber, boolean friday) {
        this.date = date;
        this.colNumber = colNumber;
        this.friday = friday;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColNumber() {
        return colNumber;
    }

    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    @Override
    public String toString() {
        return "DateModel{" +
                "date='" + date + '\'' +
                ", colNumber=" + colNumber +
                '}';
    }
}
