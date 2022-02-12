package com.quickbase.devint.controller;

import com.quickbase.devint.dto.CountryPopulationDTO;
import com.quickbase.devint.service.CountryStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class CountryStatisticsController {

    private final static Logger logger = LoggerFactory.getLogger(CountryStatisticsController.class);
    private final CountryStatisticsService countryStatisticsService;

    public CountryStatisticsController(CountryStatisticsService countryStatisticsService) {
        this.countryStatisticsService = countryStatisticsService;
    }

    @GetMapping("/api/countries/population-data")
    public ResponseEntity<Set<CountryPopulationDTO>> getPopulationData() {
        logger.info("Country statistics");
        try {
            return ResponseEntity.ok(countryStatisticsService.listPopulationByCountry());
        } catch (Exception e) {
            logger.error("Error getting country statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
