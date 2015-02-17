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

import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertTrue;

public class ITTest {

    private NotifierMock notifier;
    private CellListener cellListener;

    @Before
    public void setUp() throws Exception {
        notifier = new NotifierMock();
        cellListener = Builder.createListener(new PragueStations(), notifier, 100000);
    }

    private static class TestRecord {
        public final int cid;
        public final int lac;

        public TestRecord(int cid, int lac) {
            this.cid = cid;
            this.lac = lac;
        }

        private TestRecord() {
            this.cid = -2;
            this.lac = -2;
        }

        public static TestRecord DISCONNECT = new TestRecord();
    }

    private List<TestRecord> readLines(String name) throws ParseException {
        InputStream stream = ITTest.class.getClassLoader().getResourceAsStream(name);
        String str = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
        List<TestRecord> result = new ArrayList<>();
        JSONArray array = (JSONArray)JSONValue.parse(str);
        for (Object item : array) {
            JSONObject obj = (JSONObject)((JSONObject)item).get("cellLocation");
            if (obj.get("gsm") != null) {
                obj = (JSONObject)obj.get("gsm");
                long cid = (Long)obj.get("cid");
                long lac = (Long)obj.get("lac");
                result.add(new TestRecord((int)cid,(int)lac));
            } else {
                result.add(TestRecord.DISCONNECT);
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

        List<TestRecord> testRecords = readLines("vodafone_A.log");
        for (TestRecord record : testRecords) {
            if (record != TestRecord.DISCONNECT) {
                cellListener.cellInfo(record.cid, record.lac);
            } else {
                cellListener.disconnected();
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
}
