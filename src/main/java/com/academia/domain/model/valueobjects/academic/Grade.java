package com.academia.domain.model.valueobjects.academic;
import lombok.Value;

@Value
public class Grade {
    double value;
    public Grade(double value) {
        if (value < 0.0 || value > 100.0) { // Usamos escala 0-100 para mayor precisión
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 100.");
        }
        this.value = value;
    }
    public boolean isPassing(double passingThreshold) {
        return value >= passingThreshold;
    }
}
