package com.example.report.Model;

/**
 * Created by Muiduzzaman Lipu on 09-Feb-19.
 */
public class AssetLocation {
    private int locationId;
    private String locationName;
    private int colNumber;
    private int rowNumber;

    public AssetLocation() {
    }

    public AssetLocation(int locationId, String locationName, int colNumber, int rowNumber) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.colNumber = colNumber;
        this.rowNumber = rowNumber;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getColNumber() {
        return colNumber;
    }

    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    @Override
    public String toString() {
        return "AssetLocation{" +
                "locationId=" + locationId +
                ", locationName='" + locationName + '\'' +
                ", colNumber=" + colNumber +
                '}';
    }
}
