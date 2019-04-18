package com.example.report.Model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by Muiduzzaman Lipu on 07-Feb-19.
 */
public class AssetPlan {
    private long id;

    @DateTimeFormat( pattern="yyyy-MM-dd")
    private String date;

    private String styleNo;
    private int locationId;
    private int categoryId;
    private int subCategoryId;
    private int numberOfAsset;
    private int colNumber;

    public AssetPlan() {
    }

    public AssetPlan(long id, String date, String styleNo, int locationId, int categoryId, int subCategoryId, int numberOfAsset, int colNumber) {
        this.id = id;
        this.date = date;
        this.styleNo = styleNo;
        this.locationId = locationId;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.numberOfAsset = numberOfAsset;
        this.colNumber = colNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStyleNo() {
        return styleNo;
    }

    public void setStyleNo(String styleNo) {
        this.styleNo = styleNo;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public int getNumberOfAsset() {
        return numberOfAsset;
    }

    public void setNumberOfAsset(int numberOfAsset) {
        this.numberOfAsset = numberOfAsset;
    }

    public int getColNumber() {
        return colNumber;
    }

    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }

    @Override
    public String toString() {
        return "AssetPlan{" +
                "id=" + id +
                ", date=" + date +
                ", styleNo='" + styleNo + '\'' +
                ", locationId=" + locationId +
                ", categoryId=" + categoryId +
                ", subCategoryId=" + subCategoryId +
                ", numberOfAsset=" + numberOfAsset +
                ", colNumber=" + colNumber +
                '}';
    }
}
