package cz.prochy.metrostation.tracking;

public interface Stations {
    public boolean isStation(int cellId, int lac);
    public Station getStation(int cellId, int lac);
}
