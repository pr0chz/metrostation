package cz.prochy.metrostation;

import java.util.HashMap;
import java.util.Map;

public class Stations {

	private final static String VODAFONE = "vodafone";
	private final static String TMOBILE = "t-mobile";
	private final static String O2 = "o2";
	private final static String UNKNOWN = "unknown";
	
	private static int id(String op, int cid, int lac) {
		return cid;
	}
	
	private static void station(String name, int ... ids) {
		for (int id : ids) {
			cellMap.put(id, name);
		}
	}
	
	private static final Map<Integer, String> cellMap = new HashMap<Integer, String>();

	static {
		
		/*
		 * Metro A
		 * Vodafone - done
		 * Tmobile - done
		 * O2 - missing depo hostivar a range Mustek-Dejvicka
		 */
		station("Depo Hostivař", 
				id(VODAFONE, 115607, 38300), 
				id(VODAFONE, 14853, 38400),
				id(TMOBILE, 1988, 21820),
				id(TMOBILE, 6671205, 21820),
				id(TMOBILE, 6668949, 21820),
				id(TMOBILE, 6666419, 21820)
				);
		station("Skalka", 
				id(VODAFONE, 18812, 34300),
				id(TMOBILE, 2216, 21780),
				id(O2, 21098, 1139)
				);
		station("Strašnická",
				id(VODAFONE, 18809, 34300),
				id(TMOBILE, 2215, 21780),
				id(O2, 21099, 1139)
				);
		station("Želivského",
				id(VODAFONE, 18811, 34300),
				id(TMOBILE, 2214, 21780),
				id(O2, 21397, 1139)
				);
		station("Flora",
				id(VODAFONE, 18808, 34300),
				id(TMOBILE, 2213, 21780),
				id(O2, 21398, 1139)
				);
		station("Jiřího z Poděbrad",
				id(VODAFONE, 18810, 34300),
				id(TMOBILE, 2212, 21780),
				id(O2, 21399, 1139)
				);
		station("Náměstí Míru",
				id(VODAFONE, 18807, 34300),
				id(TMOBILE, 2211, 21780),
				id(O2, 21298, 1139)
				);
		station("Muzeum",
				id(VODAFONE, 18806, 34300),
				id(TMOBILE, 2210, 21780),
				id(O2, 21194, 1139)
				);
		station("Můstek",
				id(VODAFONE, 18805, 34300),
				id(TMOBILE, 2209, 21780)
				);
		station("Staroměstská",
				id(VODAFONE, 18804, 34300),
				id(TMOBILE, 2208, 21780)
				);
		station("Malostranská",
				id(VODAFONE, 18802, 34300),
				id(TMOBILE, 2207, 21780)
				);
		station("Hradčanská",
				id(VODAFONE, 18803, 34300),
				id(TMOBILE, 2206, 21780)
				);
		station("Dejvická",
				id(VODAFONE, 18801, 34300),
				id(TMOBILE, 2205, 21780)
				);
		
		/*
		 * Metro C
		 * Vodafone - done
		 * Tmobile - done with conflicts
		 * O2 - missing
		 */
		
		station("Háje",
				id(VODAFONE, 18842, 34300),
				id(TMOBILE, 2806, 21780)
				);
		station("Opatov",
				id(VODAFONE, 18856, 34300),
				id(TMOBILE, 2800, 21780) // non-unique
				);
		station("Chodov",
				id(VODAFONE, 18847, 34300),
				id(TMOBILE, 2802, 21780) // non-unique
				);
		station("Roztyly",
				id(VODAFONE, 18845, 34300),
				id(TMOBILE, 2801, 21780) // non-unique
				);
		station("Kačerov",
				id(VODAFONE, 18855, 34300),
				id(TMOBILE, 2800, 21780) // same as Opatov
				);
		station("Budějovická",
				id(VODAFONE, 18844, 34300),
				id(UNKNOWN, 6668472, 21820),
				id(UNKNOWN, 6668475, 21820),
				id(TMOBILE, 2801, 21780), // same as Roztyly
				id(TMOBILE, 2928, 21820)
				);
		station("Pankrác",
				id(VODAFONE, 18846, 34300),
				id(TMOBILE, 2802, 21780) // same as Chodov
				);
		station("Pražského povstání",
				id(VODAFONE, 18843, 34300),
				id(TMOBILE, 2801, 21780) // same as Roztyly and Budejovicka
				);
		station("Vyšehrad",
				id(VODAFONE, 18854, 34300),
				id(VODAFONE, 380273, 34300),
				id(TMOBILE, 2804, 21780), // same as Muzeum
				id(TMOBILE, 8221, 21780)
				);
		station("I.P.Pavlova",
				id(VODAFONE, 18841, 34300),
				id(TMOBILE, 2803, 21780)
				);
		station("Muzeum",
				id(VODAFONE, 18853, 34300),
				id(TMOBILE, 2804, 21780) // same as Vysehrad
				);
		station("Hlavní nádraží",
				id(VODAFONE, 526594, 34300),
				id(VODAFONE, 379012, 34300),
				id(VODAFONE, 18839, 34300),
				id(TMOBILE, 2425, 21780), // same as vltavska
				id(TMOBILE, 2691, 21780)
				);		
		station("Florenc",
				id(VODAFONE, 18836, 34300),
				id(TMOBILE, 2258, 21780)
				);
		station("Vltavská",
				id(VODAFONE, 18838, 34300),
				id(TMOBILE, 2425, 21780) // same as hlavni nadrazi
				);
		station("Nádraží Holešovice",
				id(VODAFONE, 18835, 34300),
				id(VODAFONE, 19121, 34700),
				id(TMOBILE, 2256, 21780)
				);
		station("Kobylisy",
				id(VODAFONE, 18837, 34300),
				id(TMOBILE, 2255, 21780)
				);
		station("Ládví",
				id(VODAFONE, 18840, 34300),
				id(TMOBILE, 2425, 21780) // same as hlavni nadrazi and vltavska
				);
		station("Střížkov",
				id(VODAFONE, 12002, 34700),
				id(VODAFONE, 186674, 34700),
				id(UNKNOWN, 203717516, 1182),
				id(TMOBILE, 2026, 16434),
				id(TMOBILE, 6736045, 16434),
				id(TMOBILE, 6730291, 21780)
				);
		station("Prosek",
				id(VODAFONE, 18849, 34300),
				id(TMOBILE, 2435, 21780)
				);
		station("Letňany",
				id(VODAFONE, 18850, 34300),
				id(TMOBILE, 2436, 21780)
				);
		
	}
	
	public static boolean isStation(int cellId) {
		return cellMap.containsKey(cellId);
	}
	
	public static String getName(int cellId) {
		return cellMap.get(cellId);
	}
	
	
}
