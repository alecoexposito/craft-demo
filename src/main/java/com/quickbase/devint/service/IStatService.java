package com.quickbase.devint.service;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public interface IStatService {
	
	List<Pair<String, Integer>> GetCountryPopulations();

}
