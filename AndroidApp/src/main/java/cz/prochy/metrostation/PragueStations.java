package cz.prochy.metrostation;

import cz.prochy.metrostation.tracking.Stations;

import java.util.HashMap;
import java.util.Map;

public class PragueStations implements Stations {

	// TODO change map to use also LAC
	
	private final static String VODAFONE = "vodafone";
	private final static String TMOBILE = "t-mobile";
	private final static String O2 = "o2";
	private final static String UNKNOWN = "unknown";
	
	private int id(String op, int cid, int lac) {
		return cid;
	}
	
	private void station(String name, int ... ids) {
		for (int id : ids) {
			cellMap.put(id, name);
		}
	}
	
	private final Map<Integer, String> cellMap = new HashMap<Integer, String>();

	{
		
		/*
		 * Metro A
		 * Vodafone - done OK
		 * Tmobile - done OK
		 * O2 - done OK
		 */
		station("Depo Hostivař", 
				id(VODAFONE, 115607, 38300), 
				id(VODAFONE, 14853, 38400),
				id(TMOBILE, 1988, 21820),
				id(TMOBILE, 6671205, 21820),
				id(TMOBILE, 6668949, 21820),
				id(TMOBILE, 6666419, 21820),
				id(O2, 1097, 1146),
				id(O2, 11015, 1146),
				id(O2, 203781758, 1146),
				id(O2, 203781755, 1146),
				id(O2, 10307073, 1146)
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
				id(TMOBILE, 2209, 21780),
				id(O2, 21195, 1139),
				id(O2, 21192, 1139)
				);
		station("Staroměstská",
				id(VODAFONE, 18804, 34300),
				id(TMOBILE, 2208, 21780),
				id(O2, 21196, 1139)
				);
		station("Malostranská",
				id(VODAFONE, 18802, 34300),
				id(TMOBILE, 2207, 21780),
				id(O2, 21197, 1139)
				);
		station("Hradčanská",
				id(VODAFONE, 18803, 34300),
				id(TMOBILE, 2206, 21780),
				id(O2, 21198, 1139)
				);
		station("Dejvická",
				id(VODAFONE, 18801, 34300),
				id(TMOBILE, 2205, 21780),
				id(O2, 21199, 1139)
				);
		
		/*
		 * Metro C
		 * Vodafone - done OK
		 * Tmobile - done with conflicts
		 * O2 - done OK
		 */
		
		station("Háje",
				id(VODAFONE, 18842, 34300),
				id(TMOBILE, 2806, 21780),
				id(O2, 21491, 1139)
				);
		station("Opatov",
				id(VODAFONE, 18856, 34300),
				id(TMOBILE, 2800, 21780),
				id(O2, 21492, 1139) 
				);
		station("Chodov",
				id(VODAFONE, 18847, 34300),
				id(TMOBILE, 2802, 21780),
				id(O2, 21493, 1139) 
				);
		station("Roztyly",
				id(VODAFONE, 18845, 34300),
				id(TMOBILE, 2801, 21780),
				id(O2, 21494, 1139) 
				);
		station("Kačerov",
				id(VODAFONE, 18855, 34300),
				id(TMOBILE, 2800, 21780),
				id(O2, 21495, 1139),
				id(O2, 1425, 1145)  
				);
		station("Budějovická",
				id(VODAFONE, 18844, 34300),
				id(UNKNOWN, 6668472, 21820),
				id(UNKNOWN, 6668475, 21820),
				id(TMOBILE, 2801, 21780),
				id(TMOBILE, 2928, 21820),
				id(O2, 21496, 1139) 
				);
		station("Pankrác",
				id(VODAFONE, 18846, 34300),
				id(TMOBILE, 2802, 21780),
				id(O2, 21497, 1139)  
				);
		station("Pražského povstání",
				id(VODAFONE, 18843, 34300),
				id(TMOBILE, 2801, 21780),
				id(O2, 21498, 1139)  
				);
		station("Vyšehrad",
				id(VODAFONE, 18854, 34300),
				id(VODAFONE, 380273, 34300),
				id(TMOBILE, 2804, 21780),
				id(TMOBILE, 8221, 21780),
				id(O2, 21499, 1139),
				id(O2, 1275, 1137),
				id(O2, 10405, 1138),
				id(O2, 1263, 1137)
				);
		station("I.P.Pavlova",
				id(VODAFONE, 18841, 34300),
				id(TMOBILE, 2803, 21780),
				id(O2, 21299, 1139)  
				);
		station("Muzeum",
				id(VODAFONE, 18853, 34300),
				id(TMOBILE, 2804, 21780),
				id(O2, 21194, 1139),
				id(O2, 21198, 1139)   // maybe Hlavak
				);
		station("Hlavní nádraží",
				id(VODAFONE, 526594, 34300),
				id(VODAFONE, 379012, 34300),
				id(VODAFONE, 18839, 34300),
				id(TMOBILE, 2425, 21780),
				id(TMOBILE, 2691, 21780),
				id(O2, 21199, 1139)    
				);		
		station("Florenc",
				id(VODAFONE, 18836, 34300),
				id(TMOBILE, 2258, 21780),
				id(O2, 21899, 1139),
				id(O2, 10301440, 1138)
				);
		station("Vltavská",
				id(VODAFONE, 18838, 34300),
				id(TMOBILE, 2425, 21780),
				id(O2, 21798, 1139) 
				);
		station("Nádraží Holešovice",
				id(VODAFONE, 18835, 34300),
				id(VODAFONE, 19121, 34700),
				id(TMOBILE, 2256, 21780),
				id(O2, 185693, 34700),
				id(O2, 21799, 1139),
				id(O2, 1701, 1182)
				);
		station("Kobylisy",
				id(VODAFONE, 18837, 34300),
				id(TMOBILE, 2255, 21780),
				id(O2, 21894, 1139)
				);
		station("Ládví",
				id(VODAFONE, 18840, 34300),
				id(TMOBILE, 2425, 21780),
				id(O2, 21893, 1139) 
				);
		station("Střížkov",
				id(VODAFONE, 12002, 34700),
				id(VODAFONE, 186674, 34700),
				id(UNKNOWN, 203717516, 1182),
				id(TMOBILE, 2026, 16434),
				id(TMOBILE, 6736045, 16434),
				id(TMOBILE, 6730291, 21780),
				id(O2, 11979, 1182),
				id(O2, 203717516, 1182),
				id(O2, 10342401, 1182)
				);
		station("Prosek",
				id(VODAFONE, 18849, 34300),
				id(TMOBILE, 2435, 21780),
				id(O2, 21992, 1139),
				id(O2, 203717116, 1182)
				);
		station("Letňany",
				id(VODAFONE, 18850, 34300),
				id(TMOBILE, 2436, 21780),
				id(O2, 21991, 1139)
				);
		
		// Metro B
		// Vodafone - OK, possible problems between Stodulky and Radlicka
		// T-mobile - multiple problems:
		//   - mess between Stodulky and Smichovske nadrazi
		//   - non-unique Narodni trida - Kolbenova
		//   - some stations missing completely
		// O2 done - OK
		
		station("Zličín",
				id(VODAFONE, 18814, 34300),
				id(VODAFONE, 119268, 38100),
				id(TMOBILE, 1938, 17250),
				id(TMOBILE, 6735507, 17250),
				id(TMOBILE, 6721430, 17250),
				id(O2, 21599, 1139)
				);
		station("Stodůlky",
				id(VODAFONE, 18816, 34300),
				id(TMOBILE, 2430, 21780),
				id(O2, 21598, 1139)
				);
		station("Luka",				
				id(VODAFONE, 18818, 34300),
				id(VODAFONE, 119079, 38100),
				id(VODAFONE, 11141, 38100),
				id(VODAFONE, 119077, 38100),
				id(TMOBILE, 1241, 17230),
				id(TMOBILE, 6734813, 17230),
				id(TMOBILE, 6721393, 17230),
				id(O2, 21597, 1139),
				id(O2, 203782717, 1153)
				);
		station("Lužiny",
				id(VODAFONE, 18820, 34300),
				id(VODAFONE, 118668, 38100),
				id(VODAFONE, 16032, 38100),
				id(TMOBILE, 6732124, 17230),
				id(O2, 21596, 1139),
				id(O2, 203783280, 1153),
				id(O2, 203782702, 1153),
				id(O2, 1523, 1153)
				);
		station("Hůrka",
				id(VODAFONE, 16062, 38100),
				id(VODAFONE, 118807, 38100),
				id(VODAFONE, 13341, 38100),
				id(VODAFONE, 18821, 34300),
				id(TMOBILE, 1924, 17230),
				id(TMOBILE, 453, 17230),
				id(TMOBILE, 1244, 17230),
				id(TMOBILE, 1245, 17230),
				id(TMOBILE, 6736893, 17230),
				id(TMOBILE, 6721459, 17230),
				id(TMOBILE, 6736958, 17230),
				id(TMOBILE, 6721479, 17230),
				id(TMOBILE, 6721461, 17230),
				id(TMOBILE, 2431, 21780),
				id(O2, 21595, 1139)
				);
		station("Nové Butovice",
				id(VODAFONE, 18823, 34300),
				id(O2, 21594, 1139)
				// missing t-mobile
				);
		station("Jinonice",
				id(VODAFONE, 18822, 34300),
				id(O2, 21593, 1139)
				// missing tmobile
				);
		station("Radlická",
				id(VODAFONE, 18824, 34300),
				id(TMOBILE, 2431, 21780),
				id(O2, 21592, 1139)
				);
		station("Smíchovské nádraží",
				id(VODAFONE, 18819, 34300),
				id(TMOBILE, 2805, 21780),
				id(O2, 21591, 1139)
				);
		station("Anděl",
				id(VODAFONE, 18817, 34300),
				id(O2, 21590, 1139)
				// missing tmobile
				);
		station("Karlovo náměstí",
				id(VODAFONE, 18815, 34300),
				id(TMOBILE, 2432, 21780),
				id(O2, 21297, 1139)
				);
		station("Národní třída",
				id(VODAFONE, 18813, 34300),
				id(TMOBILE, 2433, 21780),
				id(O2, 21193, 1139)
				);
		station("Můstek",
				id(VODAFONE, 18825, 34300),
				id(TMOBILE, 2434, 21780),
				id(TMOBILE, 2209, 21780),
				id(O2, 21192, 1139)
				);
		station("Náměstí Republiky",
				id(VODAFONE, 18851, 34300),
				id(TMOBILE, 2433, 21780),
				id(O2, 21191, 1139)
				);
		
		// block of non-unique t-mobile cells
		station("Florenc",
				id(VODAFONE, 18827, 34300),
				id(TMOBILE, 2428, 21780),
				id(O2, 21898, 1139)
				);
		station("Křižíkova",
				id(VODAFONE, 18830, 34300),
				id(TMOBILE, 2427, 21780),
				id(O2, 21897, 1139)
				);
		station("Invalidovna",
				id(VODAFONE, 18828, 34300),
				id(TMOBILE, 2428, 21780),
				id(O2, 21896, 1139)
				);
		station("Palmovka",
				id(VODAFONE, 18831, 34300),
				id(TMOBILE, 2427, 21780),
				id(O2, 21895, 1139)
				);
		station("Českomoravská",
				id(VODAFONE, 18829, 34300),
				id(TMOBILE, 2428, 21780),
				id(O2, 21999, 1139)
				);
		station("Vysočanská",
				id(VODAFONE, 18832, 34300),
				id(TMOBILE, 2427, 21780),
				id(O2, 21998, 1139)
				);
		// end of block
		
		station("Kolbenova",
				id(VODAFONE, 18833, 34300),
				id(TMOBILE, 2429, 21780),
				id(O2, 21997, 1139)
				);
		station("Hloubětín",
				id(VODAFONE, 18834, 34300),
				id(TMOBILE, 2429, 21780),
				id(O2, 21996, 1139)
				);
		station("Rajská zahrada",
				id(VODAFONE, 12764, 38400),
				id(VODAFONE, 115952, 38400),
				id(VODAFONE, 115949, 38400),
				id(TMOBILE, 8258, 20500),
				id(TMOBILE, 6670613, 20500),
				id(TMOBILE, 6670614, 20500),
				id(O2, 21995, 1139),
				id(O2, 1905, 1131),
				id(O2, 203717688, 1131)
				);
		station("Černý most",
				id(VODAFONE, 115948, 38400),
				id(VODAFONE, 115957, 38400),
				id(VODAFONE, 115958, 38400),
				id(VODAFONE, 116009, 38400),
				id(VODAFONE, 116012, 38400),
				id(VODAFONE, 115947, 38400),
				id(TMOBILE, 6669305, 20500),
				id(TMOBILE, 6669303, 20500),
				id(TMOBILE, 6670635, 20500),
				id(TMOBILE, 6670969, 20500),
				id(TMOBILE, 6670634, 20500),
				id(TMOBILE, 6668679, 20500),
				id(O2, 1954, 1131),
				id(O2, 203717002, 1131),
				id(O2, 203718138, 1131)
				);
		
		// TODO remove this
		station("Test",
				id(VODAFONE, 116348, 38300));
		
		
	}

    @Override
	public boolean isStation(int cellId, int lac) {
		return cellMap.containsKey(cellId);
	}

    @Override
	public String getName(int cellId, int lac) {
		return cellMap.get(cellId);
	}

	
}
