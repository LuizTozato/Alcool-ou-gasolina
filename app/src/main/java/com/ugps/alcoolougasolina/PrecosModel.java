package com.ugps.alcoolougasolina;

public class PrecosModel {
    private final double gasolina, etanol;

    public PrecosModel(double gasolina, double etanol) {
        this.gasolina = gasolina;
        this.etanol = etanol;
    }

    public double getGasolina() {
        return gasolina;
    }

    public double getEtanol() {
        return etanol;
    }
}
