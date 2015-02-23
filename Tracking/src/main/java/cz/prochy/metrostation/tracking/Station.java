package cz.prochy.metrostation.tracking;

import net.jcip.annotations.NotThreadSafe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
public class Station {

    private final String name;

    // TODO use something different than linebuilder here
    private final Set<LineBuilder> lines = new HashSet<>();
    private final HashMap<LineBuilder, Station> prev = new HashMap<>();
    private final HashMap<LineBuilder, Station> next = new HashMap<>();

    public Station(String name) {
        this.name = name;
    }

    public void addLine(LineBuilder lineBuilder) {
        lines.add(lineBuilder);
    }

    public Set<LineBuilder> getLines() {
        return lines;
    }

    public boolean isTransfer() {
        return lines.size() > 1;
    }

    public Set<Station> getPrev() {
        return new HashSet<>(prev.values());
    }

    public void addPrev(LineBuilder line, Station prev) {
        this.prev.put(line, prev);
    }

    public Set<Station> getNext() {
        return new HashSet<>(next.values());
    }

    public void addNext(LineBuilder line, Station next) {
        this.next.put(line, next);
    }

    public String getName() {
        return name;
    }

}
