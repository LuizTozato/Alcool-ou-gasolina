package com.ugps.alcoolougasolina.utils;

import android.widget.EditText;

import com.ugps.alcoolougasolina.interfaces.ErrorListener;

public class EditTextErrorListener implements ErrorListener {

    private final EditText editText;

    public EditTextErrorListener(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onError(String error) {
        editText.setError(error);
    }
}
