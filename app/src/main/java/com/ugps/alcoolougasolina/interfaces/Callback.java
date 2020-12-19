package com.ugps.alcoolougasolina.interfaces;

public interface Callback<T> {
    void onResult(T data);

    void onError(String error);
}
