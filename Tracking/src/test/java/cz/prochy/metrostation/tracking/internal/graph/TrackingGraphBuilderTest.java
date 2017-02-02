package cz.prochy.metrostation.tracking.internal.graph;

import cz.prochy.metrostation.tracking.graph.GraphBuilder;
import cz.prochy.metrostation.tracking.graph.LineBuilder;
import cz.prochy.metrostation.tracking.internal.Station;
import cz.prochy.metrostation.tracking.internal.StationGroup;
import cz.prochy.metrostation.tracking.internal.graph.StationGraph;
import cz.prochy.metrostation.tracking.internal.graph.TrackingGraphBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

public class TrackingGraphBuilderTest {

    private GraphBuilder<StationGraph> builder;
    private LineBuilder lineBuilder;
    private LineBuilder lineBuilder2;

    private final static String STATION1_NAME = "Station1";
    private final static String STATION2_NAME = "Station2";

    private final static String CARRIER = "a";

    @Before
    public void setUp() throws Exception {
        builder = new TrackingGraphBuilder();
        lineBuilder = builder.newLine("MetroA");
        lineBuilder2 = builder.newLine("MetroB");
    }

    @Test(expected = NullPointerException.class)
    public void nullStationNameShouldThrow() throws Exception {
        lineBuilder.station(null);
    }

    @Test
    public void stationAppearsInTheGraph() throws Exception {
        lineBuilder.station(STATION1_NAME).id(CARRIER, 1, 1);
        StationGroup group = builder.build().getStations(1, 1);
        assertThat(group.hasSingleValue(), is(true));
        Station station = group.getStation();
        assertThat(station.getName(), is(STATION1_NAME));
        assertThat(station.getLines().size(), is(1));
        assertThat(station.getNext().isEmpty(), is(true));
        assertThat(station.getPrev().isEmpty(), is(true));
        assertThat(station.isTransfer(), is(false));
    }

    @Test
    public void stationsOnTheSameLineAreConnected() throws Exception {
        lineBuilder.station(STATION1_NAME).id(CARRIER, 1, 1);
        lineBuilder.station(STATION2_NAME).id(CARRIER, 2, 2);

        StationGraph graph = builder.build();
        Station station1 = graph.getStations(1, 1).getStation();
        Station station2 = graph.getStations(2, 2).getStation();
        assertThat(station1.getPrev().isEmpty(), is(true));
        assertThat(station1.getNext().size(), is(1));
        assertThat(station1.getNext().iterator().next(), is(station2));
        assertThat(station2.getPrev().size(), is(1));
        assertThat(station2.getPrev().iterator().next(), is(station1));
        assertThat(station2.getNext().isEmpty(), is(true));

        assertThat(station1.getLines(), is(station2.getLines()));
        assertThat(station1.isTransfer(), is(false));
        assertThat(station2.isTransfer(), is(false));
    }

    @Test
    public void stationsOnDifferentLinesAreNotConnected() throws Exception {
        lineBuilder.station(STATION1_NAME).id(CARRIER, 1, 1);
        lineBuilder2.station(STATION2_NAME).id(CARRIER, 2, 2);

        StationGraph graph = builder.build();
        Station station1 = graph.getStations(1, 1).getStation();
        Station station2 = graph.getStations(2, 2).getStation();
        assertThat(station1.getPrev().isEmpty(), is(true));
        assertThat(station1.getNext().isEmpty(), is(true));
        assertThat(station2.getPrev().isEmpty(), is(true));
        assertThat(station2.getNext().isEmpty(), is(true));

        assertThat(station1.getLines(), is(not(station2.getLines())));
        assertThat(station1.isTransfer(), is(false));
        assertThat(station2.isTransfer(), is(false));
    }

    @Test
    public void testTransferStationIsDetectedByName() throws Exception {
        lineBuilder.station(STATION1_NAME).id(CARRIER, 1, 1);
        lineBuilder2.station(STATION1_NAME).id(CARRIER, 2, 2);

        StationGraph graph = builder.build();
        Station station1 = graph.getStations(1, 1).getStation();
        Station station2 = graph.getStations(2, 2).getStation();

        assertThat(station1.isTransfer(), is(true));
        assertThat(station2.isTransfer(), is(true));
    }

    @Test
    public void sameStationAddedMultipleTimesIsOk() throws Exception {
        lineBuilder.station(STATION1_NAME).id(CARRIER, 1, 1);
        lineBuilder.station(STATION1_NAME).id(CARRIER, 2, 2);
    }

    @Test
    public void chainOfMultipleStationsOnSameLine() throws Exception {
        final int testSize = 10;
        lineBuilder.station("first").id(CARRIER, 1000, 1000);
        for (int i=0; i<testSize; i++) {
            lineBuilder.station(Integer.toString(i)).id(CARRIER, i, i);
        }
        Station station = builder.build().getStations(1000, 1000).getStation();
        int counter = 0;
        while (!station.getNext().isEmpty() || counter > testSize * 10) {
            station = station.getNext().iterator().next();
            ++counter;
        }
        assertEquals(testSize, counter);
        assertEquals("9", station.getName());
    }
}