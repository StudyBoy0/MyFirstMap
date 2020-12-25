package com.example.mymap;

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
