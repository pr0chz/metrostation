package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;
import cz.prochy.metrostation.tracking.LineBuilder;
import cz.prochy.metrostation.tracking.Station;

import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class StationGroup {

    private final static StationGroup EMPTY_GROUP = new StationGroup().immutable();

    private final Set<Station> set;

    private class Immutable extends StationGroup {
        public Immutable(StationGroup group) {
            super(group);
        }

        @Override
        public void add(Station station) {
            throw new UnsupportedOperationException("Object is immutable!");
        }
    }

    public StationGroup() {
        this.set = new HashSet<>();
    }

    public StationGroup(StationGroup group) {
        this.set = new HashSet(group.set);
    }

    public void add(Station station) {
        set.add(Check.notNull(station));
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }



    public StationGroup immutable() {
        if (this instanceof Immutable) {
            return this;
        }
        return new Immutable(this);
    }

    public static StationGroup empty() {
        return EMPTY_GROUP;
    }

    public static StationGroup from(Station ... stations) {
        StationGroup group = new StationGroup();
        for (Station station : stations) {
            group.add(station);
        }
        return group;
    }

    public boolean hasSingleValue() {
        return set.size() == 1;
    }

    public boolean hasMultipleValues() {
        return set.size() > 1;
    }

    public Station getStation() {
        if (!hasSingleValue()) {
            throw new NoSuchElementException("There is no single station to be returned.");
        }
        return set.iterator().next();
    }

    public StationGroup intersect(StationGroup other) {
        StationGroup s = new StationGroup(this);
        s.set.retainAll(other.set);
        return s;
    }

    public boolean intersects(StationGroup other) {
        for (Station station : set) {
            if (other.set.contains(station)) {
                return true;
            }
        }
        return false;
    }

    public StationGroup left() {
        StationGroup s = new StationGroup();
        for (Station station : set) {
            s.set.addAll(station.getPrev());
        }
        return s;
    }

    public StationGroup right() {
        StationGroup s = new StationGroup();
        for (Station station : set) {
            s.set.addAll(station.getNext());
        }
        return s;
    }

    public StationGroup left(LineBuilder line) {
        StationGroup s = new StationGroup();
        for (Station station : set) {
            for (Station prevStation : station.getPrev()) {
                if (prevStation.getLines().contains(line)) {
                    s.set.add(prevStation);
                }
            }
        }
        return s;
    }

    public StationGroup right(LineBuilder line) {
        StationGroup s = new StationGroup();
        for (Station station : set) {
            for (Station nextStation : station.getNext()) {
                if (nextStation.getLines().contains(line)) {
                    s.set.add(nextStation);
                }
            }
        }
        return s;
    }

    public Set<Station> asSet() {
        return Collections.unmodifiableSet(set);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StationGroup)) return false;

        StationGroup that = (StationGroup) o;

        if (!set.equals(that.set)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Station station : set) {
            builder.append(station.getName()).append(",");
        }
        builder.setLength(builder.length() - 1);
        builder.append("]");
        return builder.toString();
    }
}
