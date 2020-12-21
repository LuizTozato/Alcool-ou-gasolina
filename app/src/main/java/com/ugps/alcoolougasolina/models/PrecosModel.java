package com.ugps.alcoolougasolina.models;

import androidx.annotation.NonNull;

public class PrecosModel {
    private final String municipio;
    private final double gasolina, etanol;

    public PrecosModel(@NonNull String municipio, double gasolina, double etanol) {
        this.municipio = municipio;
        this.gasolina = gasolina;
        this.etanol = etanol;
    }

    @NonNull
    public String getMunicipio() {
        return municipio;
    }

    public double getGasolina() {
        return gasolina;
    }

    public double getEtanol() {
        return etanol;
    }

    @NonNull
    @Override
    public String toString() {
        return municipio;
    }
}
