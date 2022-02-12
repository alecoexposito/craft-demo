package com.quickbase.devint.dao.impl;

import com.quickbase.devint.dao.DBManager;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DBManagerImplIntegrationTest {

    @Autowired
    DBManager dbManager;

    @Test
    void listPopulationByCountrySizeIsCorrectTest() throws SQLException {
        List<Pair<String, Integer>> result = dbManager.listPopulationByCountry();
        assertEquals(16, result.size());
    }

    @Test
    void listPopulationByCountryUSAPopulationIsCorrectTest() {
        List<Pair<String, Integer>> result = dbManager.listPopulationByCountry();
        assertEquals(311976362, result.stream().filter(pair -> pair.getKey().equals("U.S.A.")).findFirst().get().getValue());
    }

}