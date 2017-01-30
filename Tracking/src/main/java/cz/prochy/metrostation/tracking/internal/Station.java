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
