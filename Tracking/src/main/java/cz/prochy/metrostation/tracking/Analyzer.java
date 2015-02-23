package cz.prochy.metrostation.tracking;

import cz.prochy.metrostation.tracking.internal.PragueStations;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Analyzer {

    private static class PrintingNotifier implements Notifier {
        @Override
        public void onStation(String approachingStation) {
            System.out.println("* OnStation: " + approachingStation);
        }

        @Override
        public void onUnknownStation() {
            System.out.println("* OnUnknownStation");
        }

        @Override
        public void onDisconnect(String leavingStation, String nextStation) {
            System.out.println("* OnDisconnect: " + leavingStation + " -> " + nextStation);
        }

        @Override
        public void onDisconnect(String leavingStation) {
            System.out.println("* OnDisconnect: " + leavingStation);
        }
    }

    public static class TestRecord {
        public final int cid;
        public final int lac;
        public final long ts;

        public TestRecord(long ts, int cid, int lac) {
            this.ts = ts;
            this.cid = cid;
            this.lac = lac;
        }

        private TestRecord() {
            this.cid = -2;
            this.lac = -2;
            this.ts = 0;
        }

        public static int DISCONNECT_CID = -2;

        @Override
        public String toString() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            if (cid != DISCONNECT_CID) {
                return format.format(new Date(ts)) + ": {ts: " + ts + ", cid: " + cid + ", lac: " + lac + "}";
            } else {
                return "{}";
            }
        }
    }

    public static List<TestRecord> readMsLogsLines(InputStream stream) throws ParseException {
        List<TestRecord> result = new ArrayList<>();
        String str = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
        for (String line : str.split("\n")) {
            JSONObject item = (JSONObject) JSONValue.parse(line);
            long ts = (Long)item.get("ts");
            if (item.containsKey("cid")) {
                long cid = (Long)item.get("cid");
                long lac = (Long)item.get("lac");
                result.add(new TestRecord(ts, (int)cid, (int)lac));
            } else {
                result.add(new TestRecord(ts, TestRecord.DISCONNECT_CID, 0));
            }
        }
        return result;
    }

    public static void main(String [] args) {
        long stationTimeout = TimeUnit.SECONDS.toMillis(180);
        long transferTimeout = TimeUnit.SECONDS.toMillis(90);
        PragueStations stations = new PragueStations();
        CellListener cellListener = Builder.createListener(stations, new PrintingNotifier(), stationTimeout, transferTimeout);

        List<TestRecord> testRecords = null;
        try {
            testRecords = readMsLogsLines(System.in);
            for (Analyzer.TestRecord record : testRecords) {
                if (record.cid != Analyzer.TestRecord.DISCONNECT_CID) {
                    System.out.println(record + ": " + stations.getStations(record.cid, record.lac));
                    cellListener.cellInfo(record.ts, record.cid, record.lac);
                } else {
                    System.out.println(record);
                    cellListener.disconnected(record.ts);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
