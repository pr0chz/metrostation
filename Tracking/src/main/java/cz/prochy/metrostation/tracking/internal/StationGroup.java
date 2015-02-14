package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Station;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class StationGroup {

    private final static StationGroup EMPTY_GROUP = new StationGroup().immutable();

    private final Set<Station> set;

    public StationGroup() {
        this.set = new HashSet<>();
    }

    public StationGroup(StationGroup group) {
        this.set = new HashSet(group.set);
    }

    public void add(Station station) {
        set.add(station);
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    private class Immutable extends StationGroup {
        public Immutable(StationGroup group) {
            super(group);
        }

        @Override
        public void add(Station station) {
            throw new IllegalStateException("Object is immutable!");
        }
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

    public boolean hasSingleValue() {
        return set.size() == 1;
    }

    public StationGroup toSingleValue() {
        if (hasSingleValue()) {
            return this;
        } else {
            return EMPTY_GROUP;
        }
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


}
