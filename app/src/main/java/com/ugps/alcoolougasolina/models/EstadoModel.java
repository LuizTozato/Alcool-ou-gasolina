package com.ugps.alcoolougasolina.models;

import androidx.annotation.NonNull;

public class EstadoModel {
    private final String id, nome;

    public EstadoModel(@NonNull String id, @NonNull String nome) {
        this.id = id;
        this.nome = nome;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    @NonNull
    @Override
    public String toString() {
        return nome;
    }
}
