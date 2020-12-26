package com.example.mymap.network.service;

import com.example.mymap.entity.GPSResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GPSService {
    @GET("pro/gps.json")
    Call<GPSResponse> requestGPS();

}
