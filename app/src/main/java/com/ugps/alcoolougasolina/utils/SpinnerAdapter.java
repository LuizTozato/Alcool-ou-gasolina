package com.ugps.alcoolougasolina.utils;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.Collection;

import androidx.annotation.NonNull;

public class SpinnerAdapter<T> extends ArrayAdapter<T> {

    public SpinnerAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_spinner_item);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public void setData(@NonNull Collection<T> data) {
        setNotifyOnChange(false);
        clear();
        addAll(data);
        notifyDataSetChanged();
    }

}
