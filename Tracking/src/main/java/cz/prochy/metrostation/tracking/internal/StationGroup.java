/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Check;

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

    public StationGroup left(Line line) {
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

    public StationGroup right(Line line) {
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
