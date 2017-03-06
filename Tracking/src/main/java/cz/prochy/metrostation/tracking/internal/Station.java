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

import net.jcip.annotations.NotThreadSafe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
public class Station {

    private final String name;

    // TODO use something different than linebuilder here
    private final Set<Line> lines = new HashSet<>();
    private final HashMap<Line, Station> prev = new HashMap<>();
    private final HashMap<Line, Station> next = new HashMap<>();

    public Station(String name) {
        this.name = name;
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public Set<Line> getLines() {
        return lines;
    }

    public boolean isTransfer() {
        return lines.size() > 1;
    }

    public Set<Station> getPrev() {
        return new HashSet<>(prev.values());
    }

    public void addPrev(Line line, Station prev) {
        this.prev.put(line, prev);
    }

    public Set<Station> getNext() {
        return new HashSet<>(next.values());
    }

    public void addNext(Line line, Station next) {
        this.next.put(line, next);
    }

    public String getName() {
        return name;
    }

}
