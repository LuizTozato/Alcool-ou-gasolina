package com.ugps.alcoolougasolina;

import android.widget.EditText;

class EditTextErrorListener implements ErrorListener {

    private final EditText editText;

    public EditTextErrorListener(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onError(String error) {
        editText.setError(error);
    }
}
