package com.example.mymap;

public class GPSEntity {
    private String Lat;
    private String Lng;


    public String getLat(){
        return Lat;
    }

    public String getLng(){
        return Lng;
    }

    public void setLat(String lat){
        this.Lat = lat;
    }

    public void setLng(String lng){
        this.Lng = lng;
    }

    public String toString(){
        return "GPSEntity{"+"Lat="+Lat+'\''+",Lng="+Lng+'\''+'}';
    }
}
