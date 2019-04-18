package com.example.report.Model;

/**
 * Created by Muiduzzaman Lipu on 09-Feb-19.
 */
public class AssetGroupBy {
    private int count;
    private int subCategoryId;
    private int locationId;

    public AssetGroupBy() {
    }

    public AssetGroupBy(int count, int subCategoryId, int locationId) {
        this.count = count;
        this.subCategoryId = subCategoryId;
        this.locationId = locationId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "AssetGroupBy{" +
                "count=" + count +
                ", subCategoryId=" + subCategoryId +
                ", locationId=" + locationId +
                '}';
    }
}
