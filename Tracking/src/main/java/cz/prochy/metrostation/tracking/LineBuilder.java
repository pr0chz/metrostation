package cz.prochy.metrostation.tracking;

public class LineBuilder {

    private Station last;

    public void addStation(Station station) {
        Check.notNull(station);
        if (last == null) {
            last = station;
        } else {
            last.setNext(station);
            station.setPrev(last);
            last = station;
        }
    }

}
