package com.example.mymap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GPSService {
    @GET("pro/gps.json")
    Call<GPSResponse> requestGPS();

    @GET("users/{user}/repos")
    Call<String> listRepos(@Path("user")String user);
}
