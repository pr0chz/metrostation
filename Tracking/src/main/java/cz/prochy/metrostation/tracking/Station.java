package cz.prochy.metrostation.tracking;

public class Station {

    private final String name;
    private Station prev;
    private Station next;

    public Station(String name) {
        this.name = name;
    }

    public Station getPrev() {
        return prev;
    }

    public void setPrev(Station prev) {
        this.prev = prev;
    }

    public Station getNext() {
        return next;
    }

    public void setNext(Station next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }
}
