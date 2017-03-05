package cz.prochy.metrostation.tracking;

import cz.prochy.metrostation.tracking.graph.GraphBuilder;
import cz.prochy.metrostation.tracking.graph.LineBuilder;
import cz.prochy.metrostation.tracking.internal.graph.TrackingGraphBuilder;
import cz.prochy.metrostation.tracking.internal.graph.StationGraph;

public class PragueStations {

    private final static String VODAFONE = "vodafone";
    private final static String TMOBILE = "t-mobile";
    private final static String O2 = "o2";
    private final static String UNKNOWN = "unknown";

    public static StationGraph newGraph() {
        return buildStations(new TrackingGraphBuilder());
    }

    public static <StationGraph> StationGraph buildStations(GraphBuilder<StationGraph> builder) {

        final LineBuilder lineA = builder.newLine("A");
        final LineBuilder lineB = builder.newLine("B");
        final LineBuilder lineC = builder.newLine("C");

		/*
		 * Metro A
		 */
        lineA.station("Depo Hostivař")
                .id(VODAFONE, 115607, 38300)
                .id(VODAFONE, 14853, 38400)
                .id(TMOBILE, 1988, 21820)
                .id(TMOBILE, 6656614, 21820)
                .id(TMOBILE, 6668949, 21820)
                .id(O2, 1097, 1146)
                .id(O2, 11015, 1146)
                .id(O2, 203781758, 1146)
                .id(O2, 203781755, 1146)
                .id(O2, 10307073, 1146)
        ;
        lineA.station("Skalka")
                .id(VODAFONE, 18812, 34300)
                .id(TMOBILE, 2216, 21780)
                .id(O2, 21098, 1139)
        ;
        lineA.station("Strašnická")
                .id(VODAFONE, 18809, 34300)
                .id(TMOBILE, 2215, 21780)
                .id(O2, 21099, 1139)
        ;
        lineA.station("Želivského")
                .id(VODAFONE, 18811, 34300)
                .id(TMOBILE, 2214, 21780)
                .id(O2, 21397, 1139)
        ;
        lineA.station("Flora")
                .id(VODAFONE, 18808, 34300)
                .id(TMOBILE, 2213, 21780)
                .id(O2, 21398, 1139)
        ;
        lineA.station("Jiřího z Poděbrad")
                .id(VODAFONE, 18810, 34300)
                .id(TMOBILE, 2212, 21780)
                .id(O2, 21399, 1139)
        ;
        lineA.station("Náměstí Míru")
                .id(VODAFONE, 18807, 34300)
                .id(TMOBILE, 2211, 21780)
                .id(O2, 21298, 1139)
        ;
        lineA.station("Muzeum")
                .id(VODAFONE, 18806, 34300)
                .id(TMOBILE, 2210, 21780)
                .id(O2, 21194, 1139)
        ;
        lineA.station("Můstek")
                .id(VODAFONE, 18805, 34300)
                .id(TMOBILE, 2209, 21780)
                .id(O2, 21195, 1139)
                .id(O2, 21192, 1139)
        ;
        lineA.station("Staroměstská")
                .id(VODAFONE, 18804, 34300)
                .id(TMOBILE, 2208, 21780)
                .id(O2, 21196, 1139)
        ;
        lineA.station("Malostranská")
                .id(VODAFONE, 18802, 34300)
                .id(TMOBILE, 2207, 21780)
                .id(O2, 21197, 1139)
        ;
        lineA.station("Hradčanská")
                .id(VODAFONE, 18803, 34300)
                .id(TMOBILE, 2206, 21780)
                .id(O2, 21698, 1139)
        ;
        lineA.station("Dejvická")
                .id(VODAFONE, 18801, 34300)
                .id(TMOBILE, 2205, 21780)
                .id(O2, 21699, 1139)
        ;
        lineA.station("Bořislavka")
                .id(VODAFONE, 18860, 34200)
                .id(VODAFONE, 377658, 34200)
                .id(TMOBILE, 2204, 21780)
                .id(TMOBILE, 6658240, 21780)
                .id(TMOBILE, 6658340, 21780) // guess
                .id(O2, 21697, 1139)
                .id(O2, 203697114, 1139)
                .id(O2, 10495747, 1137)
        ;
        lineA.station("Nádraží Veleslavín")
                .id(VODAFONE, 18859, 34200)
                .id(VODAFONE, 377653, 34200)
                .id(TMOBILE, 25686325, 10104)
                .id(TMOBILE, 6658239, 21780)
                .id(TMOBILE, 6658339, 21780)
                .id(O2, 10495746, 1137)
        ;
        lineA.station("Petřiny")
                .id(VODAFONE, 377656, 34200)
                .id(VODAFONE, 377652, 34200)
                .id(TMOBILE, 2202, 21780)
                .id(TMOBILE, 25686324, 10104)
                .id(TMOBILE, 6658238, 21780) // guess
                .id(TMOBILE, 6658338, 21780)
                .id(O2, 10495745, 1137)

        ;
        lineA.station("Nemocnice Motol")
                .id(VODAFONE, 18857, 34200)
                .id(VODAFONE, 377651, 34200)
                .id(VODAFONE, 377655, 34200)
                .id(VODAFONE, 380861, 39502)
                .id(TMOBILE, 6658237, 21780)
                .id(TMOBILE, 6658337, 21780) // guess
                .id(TMOBILE, 25686323, 10104)
                .id(O2, 10495744, 1137)
        ;

		/*
		 * Metro C
		 */

        lineC.station("Háje")
                .id(VODAFONE, 18842, 34300)
                .id(TMOBILE, 2806, 21780)
                .id(TMOBILE, 2270, 21780)
                .id(O2, 21491, 1139)
        ;
        lineC.station("Opatov")
                .id(VODAFONE, 18856, 34300)
                .id(TMOBILE, 2800, 21780)
                .id(TMOBILE, 2269, 21780)
                .id(O2, 21492, 1139)
        ;
        lineC.station("Chodov")
                .id(VODAFONE, 18847, 34300)
                .id(TMOBILE, 2802, 21780)
                .id(TMOBILE, 2268, 21780)
                .id(O2, 21493, 1139)
        ;
        lineC.station("Roztyly")
                .id(VODAFONE, 18845, 34300)
                .id(TMOBILE, 2801, 21780)
                .id(TMOBILE, 2267, 21780)
                .id(O2, 21494, 1139)
        ;
        lineC.station("Kačerov")
                .id(VODAFONE, 18855, 34300)
                .id(TMOBILE, 2800, 21780)
                .id(TMOBILE, 2266, 21780)
                .id(O2, 21495, 1139)
                .id(O2, 1425, 1145)
                .id(O2, 10260483, 1145)
        ;
        lineC.station("Budějovická")
                .id(VODAFONE, 18844, 34300)
                .id(VODAFONE, 13611, 38300)
                .id(TMOBILE, 6668472, 21820)
                .id(TMOBILE, 6668475, 21820)
                .id(TMOBILE, 2801, 21780)
                .id(TMOBILE, 2928, 21820)
                .id(TMOBILE, 2265, 21780)
                .id(O2, 21496, 1139)
        ;
        lineC.station("Pankrác")
                .id(VODAFONE, 18846, 34300)
                .id(TMOBILE, 2802, 21780)
                .id(TMOBILE, 2264, 21780)
                .id(O2, 21497, 1139)
        ;
        lineC.station("Pražského povstání")
                .id(VODAFONE, 18843, 34300)
                .id(TMOBILE, 2801, 21780)
                .id(TMOBILE, 2263, 21780)
                .id(O2, 21498, 1139)
        ;
        lineC.station("Vyšehrad")
                .id(VODAFONE, 18854, 34300)
                .id(VODAFONE, 380273, 34300)
                .id(TMOBILE, 2804, 21780)
                .id(TMOBILE, 8221, 21780)
                .id(TMOBILE, 2262, 21780)
                .id(TMOBILE, 371, 21780)
                .id(TMOBILE, 876, 21780)
                .id(TMOBILE, 18247, 21780)
                .id(TMOBILE, 459, 21780)
                .id(O2, 21499, 1139)
                .id(O2, 1275, 1137)
                .id(O2, 10405, 1138)
                .id(O2, 1263, 1137)
                .id(O2, 10242821, 1137)
                .id(O2, 10245124, 1138)
                .id(O2, 203715927, 1137)
                .id(O2, 10242817, 1137)
                .id(O2, 262915321, 21780)
        ;
        lineC.station("I.P.Pavlova")
                .id(VODAFONE, 18841, 34300)
                .id(VODAFONE, 14711, 34300)
                .id(TMOBILE, 2803, 21780)
                .id(TMOBILE, 2261, 21780)
                .id(O2, 21299, 1139)
        ;
        lineC.station("Muzeum")
                .id(VODAFONE, 18853, 34300)
                .id(TMOBILE, 2804, 21780)
                .id(TMOBILE, 2260, 21780)
                .id(O2, 21194, 1139)
                .id(O2, 21198, 1139)   // maybe Hlavak
        ;
        lineC.station("Hlavní nádraží")
                .id(VODAFONE, 526594, 34300)
                .id(VODAFONE, 379012, 34300)
                .id(VODAFONE, 18839, 34300)
                .id(VODAFONE, 527116, 34300)
                .id(VODAFONE, 11202, 34300)
                .id(VODAFONE, 228, 21780)
                .id(TMOBILE, 2425, 21780)
                .id(TMOBILE, 2691, 21780)
                .id(TMOBILE, 2259, 21780)
                .id(TMOBILE, 262900720, 21780)
                .id(TMOBILE, 262900560, 21780)
                .id(O2, 21199, 1139)
        ;
        lineC.station("Florenc")
                .id(VODAFONE, 18836, 34300)
                .id(VODAFONE, 510211, 34300)
                .id(VODAFONE, 382683, 34300)
                .id(TMOBILE, 2258, 21780)
                .id(O2, 21899, 1139)
                .id(O2, 10301440, 1138)
        ;
        lineC.station("Vltavská")
                .id(VODAFONE, 18838, 34300)
                .id(TMOBILE, 2425, 21780) // old
                .id(TMOBILE, 2257, 21780)
                .id(O2, 21798, 1139)
        ;
        lineC.station("Nádraží Holešovice")
                .id(VODAFONE, 18835, 34300) // old
                .id(VODAFONE, 19121, 34700) // old
                .id(VODAFONE, 493313, 34700) // old
                .id(VODAFONE, 775517, 34700)
                .id(VODAFONE, 775517, 34300)
                .id(TMOBILE, 2256, 21780)
                .id(O2, 185693, 34700)
                .id(O2, 21799, 1139)
                .id(O2, 1701, 1182)
        ;
        lineC.station("Kobylisy")
                .id(VODAFONE, 18837, 34300)
                .id(TMOBILE, 2255, 21780)
                .id(O2, 21894, 1139)
        ;
        lineC.station("Ládví")
                .id(VODAFONE, 18840, 34300)
                .id(TMOBILE, 2425, 21780) // old
                .id(TMOBILE, 2254, 21780)
                .id(O2, 21893, 1139)
        ;
        lineC.station("Střížkov")
                .id(VODAFONE, 12002, 34700)
                .id(VODAFONE, 186674, 34700)
                .id(UNKNOWN, 203717516, 1182)
                .id(TMOBILE, 6736045, 16434) // old
                .id(TMOBILE, 6730291, 21780) // old
                .id(TMOBILE, 2026, 16434)
                .id(TMOBILE, 6730291, 16434)
                .id(TMOBILE, 2253, 21780) // guess
                .id(O2, 11979, 1182)
                .id(O2, 203717516, 1182)
                .id(O2, 10342401, 1182)
                .id(O2, 10341889, 1182)
        ;
        lineC.station("Prosek")
                .id(VODAFONE, 18849, 34300)
                .id(TMOBILE, 2435, 21780) // old
                .id(TMOBILE, 6734399, 16434)
                .id(TMOBILE, 2252, 21780)
                .id(O2, 21992, 1139)
                .id(O2, 203717116, 1182)
                .id(O2, 203717117, 1182)
        ;
        lineC.station("Letňany")
                .id(VODAFONE, 18850, 34300)
                .id(TMOBILE, 2436, 21780) // old
                .id(TMOBILE, 2251, 21780)
                .id(TMOBILE, 2251, 16434)
                .id(O2, 21991, 1139)
        ;

        // Metro B

        lineB.station("Zličín")
                .id(VODAFONE, 18814, 34300)
                .id(VODAFONE, 119268, 38100)
                .id(TMOBILE, 1938, 17250) // old
                .id(TMOBILE, 6735507, 17250) // old
                .id(TMOBILE, 6721430, 17250) // old
                .id(TMOBILE, 1479, 17230)
                .id(TMOBILE, 6721465, 17230)
                .id(O2, 21599, 1139)
        ;
        lineB.station("Stodůlky")
                .id(VODAFONE, 18816, 34300)
                .id(TMOBILE, 2430, 21780) // old
                .id(TMOBILE, 2222, 21780)
                .id(O2, 21598, 1139)
        ;
        lineB.station("Luka")
                .id(VODAFONE, 18818, 34300)
                .id(VODAFONE, 119079, 38100)
                .id(VODAFONE, 11141, 38100)
                .id(VODAFONE, 119077, 38100)
                .id(TMOBILE, 1241, 17230)     // old
                .id(TMOBILE, 6734813, 17230)  // old
                .id(TMOBILE, 6721393, 17230)
                .id(TMOBILE, 2223, 21780)
                .id(O2, 21597, 1139)
                .id(O2, 203782717, 1153)
        ;
        lineB.station("Lužiny")
                .id(VODAFONE, 18820, 34300)
                .id(VODAFONE, 118668, 38100)
                .id(VODAFONE, 16032, 38100)
                .id(TMOBILE, 6732124, 17230)
                .id(TMOBILE, 6736154, 17230)
                .id(TMOBILE, 2224, 21780)
                .id(O2, 21596, 1139)
                .id(O2, 203783280, 1153)
                .id(O2, 203782702, 1153)
                .id(O2, 1523, 1153)
        ;
        lineB.station("Hůrka")
                .id(VODAFONE, 16062, 38100) // old
                .id(VODAFONE, 13341, 38100) // old
                .id(VODAFONE, 18821, 34300) // old
                .id(VODAFONE, 118807, 38100)
                .id(VODAFONE, 118809, 38100)
                .id(VODAFONE, 11121, 38100)
                .id(VODAFONE, 11123, 38100)
                .id(TMOBILE, 1924, 17230) // old
                .id(TMOBILE, 453, 17230) // old
                .id(TMOBILE, 1244, 17230) // old
                .id(TMOBILE, 1245, 17230) // old
                .id(TMOBILE, 6736893, 17230) // old
                .id(TMOBILE, 6736958, 17230) // old
                .id(TMOBILE, 6721479, 17230) // old
                .id(TMOBILE, 6721461, 17230) // old
                .id(TMOBILE, 2431, 21780) // old
                .id(TMOBILE, 6721459, 17230)
                .id(TMOBILE, 2225, 21780)
                .id(TMOBILE, 6736895, 17230)
                .id(O2, 21595, 1139)
        ;
        lineB.station("Nové Butovice")
                .id(VODAFONE, 18823, 34300)
                .id(TMOBILE, 2226, 21780)
                .id(O2, 21594, 1139)
        ;
        lineB.station("Jinonice")
                .id(VODAFONE, 18822, 34300)
                .id(TMOBILE, 2227, 21780)
                .id(O2, 21593, 1139)
        ;
        lineB.station("Radlická")
                .id(VODAFONE, 18824, 34300)
                .id(VODAFONE, 18824, 37200)
                .id(TMOBILE, 2431, 21780) // old
                .id(TMOBILE, 2228, 21780)
                .id(O2, 21592, 1139)
        ;
        lineB.station("Smíchovské nádraží")
                .id(VODAFONE, 18819, 34300)
                .id(TMOBILE, 2805, 21780) // old
                .id(TMOBILE, 2229, 21780)
                .id(O2, 21591, 1139)
        ;
        lineB.station("Anděl")
                .id(VODAFONE, 18817, 34300)
                .id(TMOBILE, 2230, 21780)
                .id(O2, 21590, 1139)
        ;
        lineB.station("Karlovo náměstí")
                .id(VODAFONE, 18815, 34300)
                .id(TMOBILE, 2432, 21780)
                .id(TMOBILE, 2231, 21780)
                .id(O2, 21297, 1139)
        ;
        lineB.station("Národní třída")
                .id(VODAFONE, 18813, 34300)
                .id(TMOBILE, 2433, 21780) // old
                .id(TMOBILE, 2232, 21780)
                .id(O2, 21193, 1139)
        ;
        lineB.station("Můstek")
                .id(VODAFONE, 18825, 34300)
                .id(TMOBILE, 2434, 21780) // old
                .id(TMOBILE, 2209, 21780) // old
                .id(TMOBILE, 2233, 21780)
                .id(O2, 21192, 1139)
        ;
        lineB.station("Náměstí Republiky")
                .id(VODAFONE, 18851, 34300)
                .id(TMOBILE, 2433, 21780) // old
                .id(TMOBILE, 2234, 21780)
                .id(O2, 21191, 1139)
        ;

        // block of non-unique t-mobile cells
        lineB.station("Florenc")
                .id(VODAFONE, 18827, 34300)
                .id(TMOBILE, 2428, 21780) // old
                .id(TMOBILE, 2235, 21780)
                .id(O2, 21898, 1139)
        ;
        lineB.station("Křižíkova")
                .id(VODAFONE, 18830, 34300)
                .id(TMOBILE, 2427, 21780)
                .id(O2, 21897, 1139)
        ;
        lineB.station("Invalidovna")
                .id(VODAFONE, 18828, 34300)
                .id(TMOBILE, 2428, 21780)
                .id(O2, 21896, 1139)
        ;
        lineB.station("Palmovka")
                .id(VODAFONE, 18831, 34300)
                .id(TMOBILE, 2427, 21780)
                .id(O2, 21895, 1139)
        ;
        lineB.station("Českomoravská")
                .id(VODAFONE, 18829, 34300)
                .id(TMOBILE, 2428, 21780)
                .id(O2, 21999, 1139)
        ;
        lineB.station("Vysočanská")
                .id(VODAFONE, 18832, 34300)
                .id(TMOBILE, 2427, 21780)
                .id(O2, 21998, 1139)
        ;
        // end of block

        lineB.station("Kolbenova")
                .id(VODAFONE, 18833, 34300)
                .id(TMOBILE, 2429, 21780)
                .id(O2, 21997, 1139)
        ;
        lineB.station("Hloubětín")
                .id(VODAFONE, 18834, 34300)
                .id(TMOBILE, 2429, 21780)
                .id(O2, 21996, 1139)
        ;
        lineB.station("Rajská zahrada")
                .id(VODAFONE, 12764, 38400)
                .id(VODAFONE, 115952, 38400)
                .id(VODAFONE, 115949, 38400)
                .id(TMOBILE, 8258, 20500)
                .id(TMOBILE, 6670613, 20500)
                .id(TMOBILE, 6670614, 20500)
                .id(O2, 21995, 1139)
                .id(O2, 1905, 1131)
                .id(O2, 203717688, 1131)
        ;
        lineB.station("Černý most")
                .id(VODAFONE, 115948, 38400)
                .id(VODAFONE, 115957, 38400)
                .id(VODAFONE, 115958, 38400)
                .id(VODAFONE, 116009, 38400)
                .id(VODAFONE, 116012, 38400)
                .id(VODAFONE, 115947, 38400)
                .id(TMOBILE, 6669305, 20500)
                .id(TMOBILE, 6669303, 20500)
                .id(TMOBILE, 6670635, 20500)
                .id(TMOBILE, 6670969, 20500)
                .id(TMOBILE, 6670634, 20500)
                .id(TMOBILE, 6668679, 20500)
                .id(O2, 1954, 1131)
                .id(O2, 203717002, 1131)
                .id(O2, 203718138, 1131)
        ;

        return builder.build();
    }

}
