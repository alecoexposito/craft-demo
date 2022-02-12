package com.quickbase.devint.service.impl;

import com.quickbase.devint.dao.DBManager;
import com.quickbase.devint.dto.CountryPopulationDTO;
import com.quickbase.devint.service.CountryStatisticsService;
import com.quickbase.devint.service.IStatService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MockitoSettings
class CountryStatisticsServiceImplTest {

    @Mock
    private IStatService iStatService;
    @Mock
    private DBManager dbManager;
    private CountryStatisticsService countryStatisticsService;
    boolean setUpIsDone = false;
    private List<Pair<String, Integer>> apiResults;
    private List<Pair<String, Integer>> dbResults;

    @BeforeEach
    void setUp() {
        if (setUpIsDone) {
            return;
        }
        countryStatisticsService = new CountryStatisticsServiceImpl(dbManager, iStatService);
        apiResults = new ArrayList<>();
        apiResults.add(new ImmutablePair<>("India", 1182105000));
        apiResults.add(new ImmutablePair<>("United Kingdom", 62026962));
        apiResults.add(new ImmutablePair<>("Chile", 17094270));
        apiResults.add(new ImmutablePair<>("Mali", 15370000));
        apiResults.add(new ImmutablePair<>("Greece", 11305118));
        apiResults.add(new ImmutablePair<>("Armenia", 3249482));

        dbResults = new ArrayList<>();
        dbResults.add(new ImmutablePair<>("U.S.A.", 311976362));
        dbResults.add(new ImmutablePair<>("Canada", 27147274));
        dbResults.add(new ImmutablePair<>("United Kingdom", 62718184));
        dbResults.add(new ImmutablePair<>("Guernsey", 61570));
        dbResults.add(new ImmutablePair<>("India", 1210854977));
        dbResults.add(new ImmutablePair<>("South Korea", 49410366));
        dbResults.add(new ImmutablePair<>("Peru", 29461933));

        setUpIsDone = true;
    }

    @Test
    void listPopulationByCountryWhenOkTotalIsCombined() throws ExecutionException, InterruptedException {
        Mockito.when(iStatService.GetCountryPopulations()).thenReturn(apiResults);
        Mockito.when(dbManager.listPopulationByCountry()).thenReturn(dbResults);

        Set<CountryPopulationDTO> result = countryStatisticsService.listPopulationByCountry();
        assertEquals(11, result.size());
    }

    @Test
    void listPopulationByCountryWhenOkCountriesAreNotRepeatedAndHaveDBPopulation() throws ExecutionException, InterruptedException {
        Mockito.when(iStatService.GetCountryPopulations()).thenReturn(apiResults);
        Mockito.when(dbManager.listPopulationByCountry()).thenReturn(dbResults);
        Set<CountryPopulationDTO> result = countryStatisticsService.listPopulationByCountry();
        long indiaCount = result
                .stream()
                .filter(dto -> dto.getCountry().equalsIgnoreCase("India"))
                .count();
        assertEquals(1, indiaCount);
        boolean indiaHasDBValue = result.stream().anyMatch(dto -> dto.getCountry().equalsIgnoreCase("India") && dto.getPopulation() == 1210854977);
        assertTrue(indiaHasDBValue);
        long ukCount = result
                .stream()
                .filter(dto -> dto.getCountry().equalsIgnoreCase("United Kingdom"))
                .count();
        assertEquals(1, ukCount);
        boolean ukHasDBValue = result.stream().anyMatch(dto -> dto.getCountry().equalsIgnoreCase("United Kingdom") && dto.getPopulation() == 62718184);
        assertTrue(ukHasDBValue);
    }

    @Test
    void listPopulationByCountryWhenDBThrowsExceptionTotalAndPopulationFromAPIAreReturned() throws ExecutionException, InterruptedException {
        Mockito.when(iStatService.GetCountryPopulations()).thenReturn(apiResults);
        Mockito.when(dbManager.listPopulationByCountry()).thenThrow(new RuntimeException("DB error"));
        Set<CountryPopulationDTO> result = countryStatisticsService.listPopulationByCountry();
        // ammount is the same as API result
        assertEquals(6, result.size());
        // value for India is the one from the API result
        assertEquals(1182105000, result.stream().filter(dto -> dto.getCountry().equalsIgnoreCase("India")).findFirst().get().getPopulation());
    }

    @Test
    void listPopulationByCountryWhenDBDelayPopulationFromDBIsTaken() throws ExecutionException, InterruptedException {
        Mockito.when(iStatService.GetCountryPopulations()).thenReturn(apiResults);
        Mockito.when(dbManager.listPopulationByCountry()).thenAnswer(new AnswersWithDelay(1000, invocation -> dbResults));

        Set<CountryPopulationDTO> result = countryStatisticsService.listPopulationByCountry();
        boolean indiaHasDBValue = result.stream().anyMatch(dto -> dto.getCountry().equalsIgnoreCase("India") && dto.getPopulation() == 1210854977);
        assertTrue(indiaHasDBValue);
        boolean ukHasDBValue = result.stream().anyMatch(dto -> dto.getCountry().equalsIgnoreCase("United Kingdom") && dto.getPopulation() == 62718184);
        assertTrue(ukHasDBValue);
    }
}