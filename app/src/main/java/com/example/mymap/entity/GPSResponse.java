package com.example.mymap.entity;

import com.example.mymap.entity.GPSEntity;

public class GPSResponse {
    private GPSEntity GPS;

    public GPSEntity getGPS(){
        return GPS;
    }

    public void setGPS(GPSEntity GPS){
        this.GPS = GPS;
    }

    public String toString(){
        return "GPSResponse{"+"GPS="+GPS+'}';
    }
}
