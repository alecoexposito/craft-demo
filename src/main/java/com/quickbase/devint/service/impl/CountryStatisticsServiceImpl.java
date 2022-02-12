package com.quickbase.devint.service.impl;

import com.quickbase.devint.dao.DBManager;
import com.quickbase.devint.dto.CountryPopulationDTO;
import com.quickbase.devint.service.CountryStatisticsService;
import com.quickbase.devint.service.IStatService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CountryStatisticsServiceImpl implements CountryStatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(CountryStatisticsService.class);
    private final DBManager dbManager;
    private final IStatService iStatService;

    public CountryStatisticsServiceImpl(DBManager dbManager, IStatService iStatService) {
        this.dbManager = dbManager;
        this.iStatService = iStatService;
    }

    /**
     * Method that aggregates the information of countries and their populations.
     * In case of repeated countries the information from the database takes preference.
     * @return Set with aggregated information of countries and their population.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public Set<CountryPopulationDTO> listPopulationByCountry() throws ExecutionException, InterruptedException {
        CompletableFuture<List<Pair<String, Integer>>> dbCompletableFuture = CompletableFuture.supplyAsync(() -> {
            logger.debug("getting the countries from db");
            return dbManager.listPopulationByCountry();
        }).exceptionally(throwable -> {
            logger.error("error getting the countries from db", throwable);
            return new ArrayList<>();
        });
        CompletableFuture<List<Pair<String, Integer>>> apiCompletableFuture = CompletableFuture.supplyAsync(() -> {
            logger.debug("getting the countries from API");
            return iStatService.GetCountryPopulations();
        }).exceptionally(throwable -> {
            logger.error("problem getting the countries from API", throwable);
            return new ArrayList<>();
        });
        CompletableFuture<Set<CountryPopulationDTO>> finalCompletableFuture = dbCompletableFuture.thenCombine(apiCompletableFuture, (pairsDB, pairsApi) -> {
            Set<CountryPopulationDTO> result = new HashSet<>();
            result.addAll(pairsDB.stream()
                    .map(pair -> CountryPopulationDTO
                            .builder()
                            .country(pair.getKey())
                            .population(pair.getValue())
                            .build()
                    ).collect(Collectors.toSet())
            );
            result.addAll(pairsApi.stream()
                    .map(pair -> CountryPopulationDTO
                            .builder()
                            .country(pair.getKey())
                            .population(pair.getValue())
                            .build()
                    ).collect(Collectors.toSet())
            );
            return result;
        });
        return finalCompletableFuture.get();
    }

}
