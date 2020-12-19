package com.ugps.alcoolougasolina.models;

import androidx.annotation.NonNull;

public class OptionModel<T> {
    private final T value;
    private final String description;

    public OptionModel(@NonNull T value, @NonNull String description) {
        this.value = value;
        this.description = description;
    }

    @NonNull
    public T getValue() {
        return value;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    @Override
    public String toString() {
        return description;
    }
}
