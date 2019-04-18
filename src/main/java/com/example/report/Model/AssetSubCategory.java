package com.example.report.Model;

/**
 * Created by Muiduzzaman Lipu on 09-Feb-19.
 */
public class AssetSubCategory {
    private int subCategoryId;
    private String subCategoryName;
    private int rowNumber;

    public AssetSubCategory() {
    }

    public AssetSubCategory(int subCategoryId, String subCategoryName, int rowNumber) {
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.rowNumber = rowNumber;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    @Override
    public String toString() {
        return "AssetSubCategory{" +
                "subCategoryId=" + subCategoryId +
                ", subCategoryName='" + subCategoryName + '\'' +
                ", rowNumber=" + rowNumber +
                '}';
    }
}
