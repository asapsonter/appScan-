package com.example.scanqrapp.models;

import java.util.Objects;

public class SingleScannedRow {
    public String uuid;
    public String building;
    public String floor;
    public String zone;
    public String uniqueId;
    public String productName;


    @Override
    public boolean equals(Object o){
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;
        SingleScannedRow that = (SingleScannedRow) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(building, that.building) && Objects.equals(floor, that.floor) && Objects.equals(zone, that.zone) && Objects.equals(uniqueId, that.uniqueId) && Objects.equals(productName, that.productName);

    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, building, floor, zone, uniqueId, productName);
    }

}
