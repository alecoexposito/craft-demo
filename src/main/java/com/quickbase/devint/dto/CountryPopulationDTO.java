package com.quickbase.devint.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * DTO with the country and the population
 */
@Getter
@Setter
@Builder
public class CountryPopulationDTO {
    private String country;
    private Integer population;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryPopulationDTO that = (CountryPopulationDTO) o;
        return country.equals(that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country);
    }
}
