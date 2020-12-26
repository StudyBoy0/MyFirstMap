package com.example.mymap.callback;

public interface CallBack<T> {
    void success(T result) ;
    void failed(String errorMsg);
}
