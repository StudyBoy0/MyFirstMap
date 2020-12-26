package com.example.mymap.entity;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TennyQ on 12/26/20
 */
public class LineDataEntity {

    private long timeTag;

    private List<LatLng> latLngList;

    public LineDataEntity(long timeTag) {
        this.timeTag = timeTag;
        latLngList = new ArrayList<>();
    }

    public void addLatLng(LatLng latLng) {
        latLngList.add(latLng);
    }

    public long getTimeTag() {
        return timeTag;
    }

    public void setTimeTag(long timeTag) {
        this.timeTag = timeTag;
    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    public void setLatLngList(List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }

    @Override
    public String toString() {
        return "DateLineEntity{" +
                "timeTag=" + timeTag +
                ", latLngList=" + latLngList +
                '}';
    }
}
