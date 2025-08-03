package com.academia.domain.model.valueobjects.academic;
import lombok.Value;
import java.time.LocalDate;
@Value
public class TermDates {
    LocalDate startDate;
    LocalDate endDate;
    public TermDates(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        this.startDate = start;
        this.endDate = end;
    }
}