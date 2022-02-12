package com.quickbase.devint.service;

import com.quickbase.devint.dto.CountryPopulationDTO;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface CountryStatisticsService {

    Set<CountryPopulationDTO> listPopulationByCountry() throws ExecutionException, InterruptedException;
}
