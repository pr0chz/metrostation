package cz.prochy.metrostation.tracking;

public class LineBuilder {

    private Station last;

    public void addStation(Station station) {
        Check.notNull(station);
        if (last == null) {
            last = station;
        } else {
            last.addNext(this, station);
            station.addPrev(this, last);
            last = station;
        }
    }

}
