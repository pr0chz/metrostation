package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Builder;
import cz.prochy.metrostation.tracking.CellListener;
import cz.prochy.metrostation.tracking.Notifier;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertTrue;

public class ITTest {

    private NotifierMock notifier;
    private CellListener cellListener;

    @Before
    public void setUp() throws Exception {
        notifier = new NotifierMock();
        cellListener = Builder.createListener(
                new PragueStations(),
                notifier,
                TimeUnit.SECONDS.toMillis(180),
                TimeUnit.SECONDS.toMillis(90));
    }

    private static class TestRecord {
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
    }

    private List<TestRecord> readLegacyLines(String name) throws ParseException {
        InputStream stream = ITTest.class.getClassLoader().getResourceAsStream(name);
        String str = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
        List<TestRecord> result = new ArrayList<>();
        JSONArray array = (JSONArray)JSONValue.parse(str);
        for (Object item : array) {
            long ts = (Long)((JSONObject)item).get("dumpTS");
            JSONObject obj = (JSONObject)((JSONObject)item).get("cellLocation");
            if (obj.get("gsm") != null) {
                obj = (JSONObject)obj.get("gsm");
                long cid = (Long)obj.get("cid");
                long lac = (Long)obj.get("lac");
                result.add(new TestRecord(ts, (int)cid,(int)lac));
            } else {
                result.add(new TestRecord(ts, TestRecord.DISCONNECT_CID, 0));
            }
        }
        return result;
    }

    private List<TestRecord> readMsLogsLines(String name) throws ParseException {
        List<TestRecord> result = new ArrayList<>();
        InputStream stream = ITTest.class.getClassLoader().getResourceAsStream(name);
        String str = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
        for (String line : str.split("\n")) {
            JSONObject item = (JSONObject)JSONValue.parse(line);
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


    private void expectUnknownStation() {
        notifier.onUnknownStation();
        expectLastCall().once();
    }

    private final static String DEJVICKA = "Dejvická";
    private final static String HRADCANSKA = "Hradčanská";
    private final static String MALOSTRANSKA = "Malostranská";
    private final static String STAROMESTSKA = "Staroměstská";
    private final static String MUSTEK = "Můstek";
    private final static String MUZEUM = "Muzeum";
    private final static String NAMESTI_MIRU = "Náměstí Míru";
    private final static String JIRIHO_Z_PODEBRAD = "Jiřího z Poděbrad";
    private final static String FLORA = "Flora";
    private final static String ZELIVSKEHO = "Želivského";
    private final static String STRASNICKA = "Strašnická";
    private final static String SKALKA = "Skalka";
    private final static String DEPO_HOSTIVAR = "Depo Hostivař";

    private final static String HLAVNI_NADRAZI = "Hlavní nádraží";
    private final static String FLORENC = "Florenc";

    private static class NotifierMock implements Notifier {
        private StringBuilder actual;
        private StringBuilder current = new StringBuilder();

        @Override
        public void onStation(String approachingStation) {
            current.append("onStation: " + approachingStation + "\n");
        }

        @Override
        public void onUnknownStation() {
            current.append("onUnknownStation\n");
        }

        @Override
        public void onDisconnect(String leavingStation, String nextStation) {
            current.append("onDisconnect: " + leavingStation + " -> " + nextStation + "\n");
        }

        @Override
        public void onDisconnect(String leavingStation) {
            current.append("onDisconnect: " + leavingStation + "\n");
        }

        public void startExpect() {
            actual = current;
            current = new StringBuilder();
        }

        private void expectChunk(String station) {
            onStation(station);
            onDisconnect(station);
        }

        private void expectChunk(String station, String prediction) {
            onStation(station);
            onDisconnect(station, prediction);
        }

        private String getCurrentCapture() {
            return current.toString();
        }

        public boolean verify() {
            boolean ok = true;
            StringBuilder result = new StringBuilder();

            String [] a = actual.toString().split("\n");
            String [] e = current.toString().split("\n");
            if (a.length != e.length) {
                result.append("FAIL: Lengths differ! Expected: " + e.length + ", actual: " + a.length + "\n");
                ok = false;
            }
            for (int i=0; i<Math.min(a.length, e.length); i++) {
                if (a[i].equals(e[i])) {
                    result.append("OK: " + a[i] + "\n");
                } else {
                    ok = false;
                    result.append("FAIL: Expected: " + e[i] + ", Actual: " + a[i] + "\n");
                }
            }
            if (a.length > e.length) {
                for (int i=e.length; i<a.length; i++) {
                    result.append("FAIL: Trailing actual: " + a[i] + "\n");
                }
            }
            if (e.length > a.length) {
                for (int i=a.length; i<e.length; i++) {
                    result.append("FAIL: Trailing expected: " + e[i] + "\n");
                }
            }

            System.out.println(result);
            return ok;
        }
    }

    @Test
    public void testVodafoneLineA() throws Exception {

        List<TestRecord> testRecords = readLegacyLines("legacy/vodafone_A.log");
        for (TestRecord record : testRecords) {
            if (record.cid != TestRecord.DISCONNECT_CID) {
                cellListener.cellInfo(record.ts, record.cid, record.lac);
            } else {
                cellListener.disconnected(record.ts);
            }
        }

        notifier.startExpect();
        notifier.expectChunk(MUSTEK);
        notifier.expectChunk(STAROMESTSKA, MALOSTRANSKA);
        notifier.expectChunk(MALOSTRANSKA, HRADCANSKA);
        notifier.expectChunk(HRADCANSKA, DEJVICKA);
        notifier.expectChunk(DEJVICKA);
        notifier.expectChunk(HRADCANSKA, MALOSTRANSKA);
        notifier.expectChunk(MALOSTRANSKA, STAROMESTSKA);
        notifier.expectChunk(STAROMESTSKA, MUSTEK);
        notifier.expectChunk(MUSTEK, MUZEUM);
        notifier.expectChunk(MUZEUM, NAMESTI_MIRU);
        notifier.expectChunk(NAMESTI_MIRU, JIRIHO_Z_PODEBRAD);
        notifier.expectChunk(JIRIHO_Z_PODEBRAD, FLORA);
        notifier.expectChunk(FLORA, ZELIVSKEHO);
        notifier.expectChunk(ZELIVSKEHO, STRASNICKA);
        notifier.expectChunk(STRASNICKA, SKALKA);
        notifier.expectChunk(SKALKA, DEPO_HOSTIVAR);
        notifier.expectChunk(DEPO_HOSTIVAR);
        notifier.onStation(SKALKA);

        assertTrue(notifier.verify());
    }

    @Test
    public void testSkalkaToFlorencAndBack() throws Exception {

        List<TestRecord> testRecords = readMsLogsLines("mslogs/ms.log");
        for (TestRecord record : testRecords) {
            if (record.cid != TestRecord.DISCONNECT_CID) {
                cellListener.cellInfo(record.ts, record.cid, record.lac);
            } else {
                cellListener.disconnected(record.ts);
            }
        }

        notifier.startExpect();
        notifier.expectChunk(SKALKA);
        notifier.expectChunk(STRASNICKA, ZELIVSKEHO);
        notifier.expectChunk(ZELIVSKEHO, FLORA);
        notifier.expectChunk(FLORA, JIRIHO_Z_PODEBRAD);
        notifier.expectChunk(JIRIHO_Z_PODEBRAD, NAMESTI_MIRU);
        notifier.expectChunk(NAMESTI_MIRU, MUZEUM);
        notifier.expectChunk(MUZEUM);
        notifier.expectChunk(HLAVNI_NADRAZI, FLORENC);
        notifier.onStation(FLORENC);
        notifier.onUnknownStation();
        notifier.expectChunk(FLORENC);
        notifier.expectChunk(HLAVNI_NADRAZI, MUZEUM);
        notifier.expectChunk(MUZEUM);
        notifier.expectChunk(NAMESTI_MIRU, JIRIHO_Z_PODEBRAD);
        notifier.expectChunk(JIRIHO_Z_PODEBRAD, FLORA);
        notifier.expectChunk(FLORA, ZELIVSKEHO);
        notifier.expectChunk(ZELIVSKEHO, STRASNICKA);
        notifier.expectChunk(STRASNICKA, SKALKA);
        notifier.onStation(SKALKA);
        notifier.onUnknownStation();

        assertTrue(notifier.verify());
    }


}
