package cz.prochy.metrostation.tracking;

import java.util.Objects;

public class LineBuilder {

    private Station last;

    public void addStation(Station station) {
        Objects.requireNonNull(station);
        if (last == null) {
            last = station;
        } else {
            last.setNext(station);
            station.setPrev(last);
            last = station;
        }
    }

}
