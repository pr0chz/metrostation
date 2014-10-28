package cz.prochy.metrostation.tracking;

public interface Stations {
    public boolean isStation(int cellId, int lac);
    public String getName(int cellId, int lac);
}
