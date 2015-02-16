package cz.prochy.metrostation.tracking;

import net.jcip.annotations.NotThreadSafe;

import java.util.Collections;
import java.util.Set;

@NotThreadSafe
public class Station {

    private final String name;
    private Set<Station> prev = Collections.emptySet();
    private Set<Station> next = Collections.emptySet();

    public Station(String name) {
        this.name = name;
    }

    public Set<Station> getPrev() {
        return prev;
    }

    public void setPrev(Station prev) {
        this.prev = Collections.singleton(prev);
    }

    public Set<Station> getNext() {
        return next;
    }

    public void setNext(Station next) {
        this.next = Collections.singleton(next);
    }

    public String getName() {
        return name;
    }

}
