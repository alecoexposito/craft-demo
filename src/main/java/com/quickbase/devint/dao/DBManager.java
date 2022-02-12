package com.quickbase.devint.dao;

import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ckeswani on 9/16/15.
 */
public interface DBManager {
    Connection getConnection();
    List<Pair<String, Integer>> listPopulationByCountry();
}
