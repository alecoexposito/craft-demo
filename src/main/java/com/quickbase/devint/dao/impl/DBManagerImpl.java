package com.quickbase.devint.dao.impl;

import com.quickbase.devint.dao.DBManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This DBManager implementation provides a connection to the database containing population data.
 *
 * Created by ckeswani on 9/16/15.
 */
@Component
public class DBManagerImpl implements DBManager {

    private static final Logger logger = LoggerFactory.getLogger(DBManagerImpl.class);

    public Connection getConnection() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:resources/data/citystatecountry.db");
            System.out.println("Opened database successfully");

        } catch (ClassNotFoundException cnf) {
            System.out.println("could not load driver");
        } catch (SQLException sqle) {
            System.out.println("sql exception:" + sqle.getStackTrace());
        }
        return c;
    }

    /**
     * Method that returns a list of countries and their population from the database
     * @return The List of countries and their population
     */
    public List<Pair<String, Integer>> listPopulationByCountry() {
        logger.info("Started listPopulationByCountry");
        String sql = "SELECT Country.CountryName, SUM(C.Population) AS Population " +
                "FROM Country " +
                "INNER JOIN State S on Country.CountryId = S.CountryId " +
                "INNER JOIN City C on S.StateId = C.StateId " +
                "GROUP BY Country.CountryId";
        List<Pair<String, Integer>> result = new ArrayList<>();
        try (
                Connection conn = this.getConnection();
                PreparedStatement prepareStatement = conn.prepareStatement(sql);
                ResultSet resultSet = prepareStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                result.add(new ImmutablePair<>(resultSet.getString("CountryName"), resultSet.getInt("Population")));
            }
        } catch (SQLException e) {
            logger.error("Problem loading countries from db", e);
        }
        logger.info("Finished listPopulationByCountry");
        return result;
    }

}
