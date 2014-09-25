package cz.prochy.metrostation;

import java.util.HashMap;
import java.util.Map;

public class Stations {

	private static final Map<Integer, String> cellMap = new HashMap<Integer, String>();
	static {
		cellMap.put(18812, ">A< Skalka");
		cellMap.put(18809, ">A< Strasnicka");
		cellMap.put(18811, ">A< Zelivskeho");
		cellMap.put(18808, ">A< Flora");
		cellMap.put(18810, ">A< Jiriho z Podebrad");
		cellMap.put(18807, ">A< Namesti Miru");
		cellMap.put(18806, ">A< Muzeum");
		cellMap.put(18853, ">C< Muzeum");
		cellMap.put(18839, ">C< Hlavni nadrazi");
		cellMap.put(116348, ">X< Test 1");
		cellMap.put(116351, ">X< Test 2");
		cellMap.put(115789, ">X< Test 3");
	}
	
	public static boolean isStation(int cellId) {
		return cellMap.containsKey(cellId);
	}
	
	public static String getName(int cellId) {
		return cellMap.get(cellId);
	}
	
	
}
