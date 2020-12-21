package com.ugps.alcoolougasolina.utils;

import android.view.View;
import android.widget.AdapterView;

public abstract class OnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public abstract void onItemSelected(int position);

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        onItemSelected(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // do nothing
    }
}
