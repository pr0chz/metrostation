package cz.prochy.metrostation.tracking;

import cz.prochy.metrostation.tracking.internal.PragueStations;
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



    private List<Analyzer.TestRecord> readLegacyLines(String name) throws ParseException {
        InputStream stream = ITTest.class.getClassLoader().getResourceAsStream(name);
        String str = new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
        List<Analyzer.TestRecord> result = new ArrayList<>();
        JSONArray array = (JSONArray)JSONValue.parse(str);
        for (Object item : array) {
            long ts = (Long)((JSONObject)item).get("dumpTS");
            JSONObject obj = (JSONObject)((JSONObject)item).get("cellLocation");
            if (obj.get("gsm") != null) {
                obj = (JSONObject)obj.get("gsm");
                long cid = (Long)obj.get("cid");
                long lac = (Long)obj.get("lac");
                result.add(new Analyzer.TestRecord(ts, (int)cid,(int)lac));
            } else {
                result.add(new Analyzer.TestRecord(ts, Analyzer.TestRecord.DISCONNECT_CID, 0));
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



    private final static String LETNANY = "Letňany";
    private final static String PROSEK = "Prosek";
    private final static String STRIZKOV = "Střížkov";
    private final static String LADVI = "Ládví";
    private final static String KOBYLISY = "Kobylisy";
    private final static String NADRAZI_HOLESOVICE = "Nádraží Holešovice";
    private final static String VLTAVSKA = "Vltavská";
    private final static String FLORENC = "Florenc";
    private final static String HLAVNI_NADRAZI = "Hlavní nádraží";
    // muzeum
    private final static String IP_PAVLOVA = "I.P.Pavlova";
    private final static String VYSEHRAD = "Vyšehrad";
    private final static String PRAZSKEHO_POVSTANI = "Pražského povstání";
    private final static String PANKRAC = "Pankrác";
    private final static String BUDEJOVICKA = "Budějovická";
    private final static String KACEROV = "Kačerov";
    private final static String ROZTYLY = "Roztyly";
    private final static String CHODOV = "Chodov";
    private final static String OPATOV = "Opatov";
    private final static String HAJE = "Háje";

    private final static String CERNY_MOST = "Černý most";
    private final static String RAJSKA_ZAHRADA = "Rajská zahrada";
    private final static String HLOUBETIN = "Hloubětín";
    private final static String KOLBENOVA = "Kolbenova";
    private final static String VYSOCANSKA = "Vysočanská";
    private final static String CESKOMORAVSKA = "Českomoravská";
    private final static String PALMOVKA = "Palmovka";
    private final static String INVALIDOVNA = "Invalidovna";
    private final static String KRIZIKOVA = "Křižíkova";
    // florenc
    private final static String NAMESTI_REPUBLIKY = "Náměstí Republiky";
    // mustek
    private final static String NARODNI_TRIDA = "Národní třída";
    private final static String KARLOVO_NAMESTI = "Karlovo náměstí";
    private final static String ANDEL = "Anděl";
    private final static String SMICHOVSKE_NADRAZI = "Smíchovské nádraží";
    private final static String RADLICKA = "Radlická";
    private final static String JINONICE = "Jinonice";
    private final static String NOVE_BUTOVICE = "Nové Butovice";
    private final static String HURKA = "Hůrka";
    private final static String LUZINY = "Lužiny";
    private final static String LUKA = "Luka";
    private final static String STODULKY = "Stodůlky";
    private final static String ZLICIN = "Zličín";

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

            if (actual == null) {
                throw new IllegalStateException("Start expect has not been called!");
            }

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

    private void replayFile(String name) throws java.text.ParseException {
        List<Analyzer.TestRecord> testRecords = readMsLogsLines(name);
        for (Analyzer.TestRecord record : testRecords) {
            if (record.cid != Analyzer.TestRecord.DISCONNECT_CID) {
                cellListener.cellInfo(record.ts, record.cid, record.lac);
            } else {
                cellListener.disconnected(record.ts);
            }
        }
    }

    private List<Analyzer.TestRecord> readMsLogsLines(String filename) throws java.text.ParseException {
        InputStream stream = ITTest.class.getClassLoader().getResourceAsStream(filename);
        return Analyzer.readMsLogsLines(stream);
    }

    @Test
    public void testSkalkaToFlorencAndBack2() throws Exception {

        replayFile("mslogs/skalka_florenc_and_back_2.log");

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
        // whole day cell not changing
        notifier.onDisconnect(FLORENC);
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

    @Test
    public void testSkalkaToFlorencAndBack1() throws Exception {

        replayFile("mslogs/skalka_florenc_and_back_1.log");

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

    @Test
    public void testFlorencHajeAndBack() throws Exception {

        // Smejky's journey home and back to work with some stops
        replayFile("mslogs/florenc_haje_1.log");

        notifier.startExpect();
        notifier.expectChunk(FLORENC);
        notifier.expectChunk(HLAVNI_NADRAZI, MUZEUM);
        notifier.expectChunk(MUZEUM, IP_PAVLOVA);
        notifier.onStation(IP_PAVLOVA);
        notifier.onUnknownStation();
        notifier.expectChunk(IP_PAVLOVA);
        notifier.onStation(VYSEHRAD);
        notifier.onUnknownStation();
        notifier.expectChunk(VYSEHRAD);
        notifier.expectChunk(PRAZSKEHO_POVSTANI, PANKRAC);
        notifier.expectChunk(PANKRAC, BUDEJOVICKA);
        notifier.expectChunk(BUDEJOVICKA, KACEROV);
        notifier.expectChunk(KACEROV, ROZTYLY);
        notifier.expectChunk(ROZTYLY, CHODOV);
        notifier.expectChunk(CHODOV, OPATOV);
        notifier.expectChunk(OPATOV, HAJE);
        notifier.onStation(HAJE);
        notifier.onUnknownStation();
        notifier.expectChunk(OPATOV);
        notifier.expectChunk(CHODOV, ROZTYLY);
        notifier.expectChunk(ROZTYLY, KACEROV);
        notifier.expectChunk(KACEROV, BUDEJOVICKA);
        notifier.expectChunk(BUDEJOVICKA, PANKRAC);
        notifier.expectChunk(PANKRAC, PRAZSKEHO_POVSTANI);
        notifier.expectChunk(PRAZSKEHO_POVSTANI, VYSEHRAD);
        notifier.expectChunk(VYSEHRAD, IP_PAVLOVA);
        notifier.expectChunk(IP_PAVLOVA, MUZEUM);
        notifier.expectChunk(MUZEUM, HLAVNI_NADRAZI);
        notifier.expectChunk(HLAVNI_NADRAZI, FLORENC);
        notifier.onStation(FLORENC);
        notifier.onUnknownStation();

        assertTrue(notifier.verify());
    }

    @Test
    public void testTMobile() throws Exception {
        // This tests the most undeterministic route for whole app, Tracker should
        // deal with this nicely.
        // Unfortunately we are missing disconnects in log so we have just onStation
        // events.
        replayFile("legacy/mslogs/tmobile.ms.log");

        // line C
        notifier.startExpect();
        notifier.onStation(IP_PAVLOVA);
        notifier.onStation(VYSEHRAD);
        notifier.onStation(PRAZSKEHO_POVSTANI);
        notifier.onStation(PANKRAC);
        notifier.onStation(BUDEJOVICKA);
        notifier.onStation(KACEROV);
        notifier.onStation(ROZTYLY);
        notifier.onStation(CHODOV);
        notifier.onStation(OPATOV);
        notifier.onStation(HAJE);
        notifier.onStation(OPATOV);
        notifier.onStation(CHODOV);
        notifier.onStation(ROZTYLY);
        notifier.onStation(KACEROV);
        notifier.onStation(BUDEJOVICKA);
        notifier.onStation(PANKRAC);
        notifier.onStation(PRAZSKEHO_POVSTANI);
        notifier.onStation(VYSEHRAD);
        notifier.onStation(IP_PAVLOVA);
        notifier.onStation(MUZEUM);
        notifier.onStation(HLAVNI_NADRAZI);
        notifier.onStation(FLORENC);
        notifier.onStation(VLTAVSKA);
        notifier.onStation(NADRAZI_HOLESOVICE);
        notifier.onStation(KOBYLISY);
        notifier.onStation(LADVI);
        notifier.onStation(STRIZKOV);
        notifier.onStation(PROSEK);
        notifier.onStation(LETNANY);

        // unfortunately valid behaviour, caused by broken cell data
        notifier.onUnknownStation();

        notifier.onStation(PROSEK);
        notifier.onStation(STRIZKOV);
        notifier.onStation(LADVI);
        notifier.onStation(KOBYLISY);
        notifier.onStation(NADRAZI_HOLESOVICE);
        notifier.onStation(VLTAVSKA);
        notifier.onStation(FLORENC);
        notifier.onStation(HLAVNI_NADRAZI);
        notifier.onStation(MUZEUM);
        notifier.onStation(IP_PAVLOVA);

        // line B
        // start on Florenc, unfortunately ambiguous cell => no OnStation
        notifier.onStation(NAMESTI_REPUBLIKY);
        notifier.onStation(MUSTEK);
        notifier.onStation(NARODNI_TRIDA);
        notifier.onStation(KARLOVO_NAMESTI);
        // Andel cell missing in source data
        notifier.onStation(SMICHOVSKE_NADRAZI);
        notifier.onStation(RADLICKA);
        // Jinonice cell missing in source data
        // Nove Butovice cell missing in source data
        notifier.onStation(HURKA);
        notifier.onStation(LUZINY);
        notifier.onStation(LUKA);
        notifier.onStation(STODULKY);
        notifier.onStation(ZLICIN);
        notifier.onStation(STODULKY);
        // Luka missing in source data
        notifier.onStation(LUZINY);
        notifier.onStation(HURKA);
        // Nove Butovice cell missing in source data
        // Jinonice cell missing in source data
        // Radlicka cell missing in source data
        notifier.onStation(SMICHOVSKE_NADRAZI);
        // Andel cell missing in source data
        notifier.onStation(KARLOVO_NAMESTI);
        notifier.onStation(NARODNI_TRIDA);
        notifier.onStation(MUSTEK);
        notifier.onStation(NAMESTI_REPUBLIKY);
        notifier.onStation(FLORENC);
        notifier.onStation(KRIZIKOVA);
        notifier.onStation(INVALIDOVNA);
        notifier.onStation(PALMOVKA);
        notifier.onStation(CESKOMORAVSKA);
        notifier.onStation(VYSOCANSKA);
        notifier.onStation(KOLBENOVA);
        // Hloubetin missing in source data
        notifier.onStation(RAJSKA_ZAHRADA);
        notifier.onStation(CERNY_MOST);
        notifier.onStation(RAJSKA_ZAHRADA);
        notifier.onStation(HLOUBETIN);
        // Kolbenova missing in source data
        notifier.onStation(VYSOCANSKA);
        notifier.onStation(CESKOMORAVSKA);
        notifier.onStation(PALMOVKA);
        notifier.onStation(INVALIDOVNA);
        notifier.onStation(KRIZIKOVA);
        notifier.onStation(FLORENC);
        notifier.onStation(NAMESTI_REPUBLIKY);
        notifier.onStation(MUSTEK);

        // line A
        notifier.onStation(STAROMESTSKA);
        notifier.onStation(MALOSTRANSKA);
        notifier.onStation(HRADCANSKA);
        notifier.onStation(DEJVICKA);

        // unfortunately valid behaviour, caused by broken cell data
        notifier.onUnknownStation();

        notifier.onStation(HRADCANSKA);
        notifier.onStation(MALOSTRANSKA);
        notifier.onStation(STAROMESTSKA);
        notifier.onStation(MUSTEK);
        notifier.onStation(MUZEUM);
        notifier.onStation(NAMESTI_MIRU);
        notifier.onStation(JIRIHO_Z_PODEBRAD);
        notifier.onStation(FLORA);
        notifier.onStation(ZELIVSKEHO);
        notifier.onStation(STRASNICKA);
        notifier.onStation(SKALKA);
        notifier.onStation(DEPO_HOSTIVAR);
        notifier.onStation(SKALKA);

        assertTrue(notifier.verify());
    }

    @Test
    public void testVodafone() throws Exception {
        replayFile("legacy/mslogs/vodafone.ms.log");

        // line C
        // this is some mess on the begining with stop on IP Pavlova
        notifier.startExpect();
        notifier.onStation(VYSEHRAD);
        notifier.onUnknownStation();
        notifier.onStation(VYSEHRAD);
        notifier.onStation(IP_PAVLOVA);
        
        // journey begins heading to Haje
        // TODO user has not left the station, so the prediction stays
        notifier.onDisconnect(IP_PAVLOVA, MUZEUM);
        notifier.expectChunk(VYSEHRAD, PRAZSKEHO_POVSTANI);
        notifier.expectChunk(PRAZSKEHO_POVSTANI, PANKRAC);
        notifier.expectChunk(PANKRAC, BUDEJOVICKA);
        notifier.expectChunk(BUDEJOVICKA, KACEROV);
        notifier.expectChunk(KACEROV, ROZTYLY);
        notifier.expectChunk(ROZTYLY, CHODOV);
        notifier.expectChunk(CHODOV, OPATOV);
        notifier.expectChunk(OPATOV, HAJE);
        notifier.expectChunk(HAJE);
        notifier.expectChunk(OPATOV, CHODOV);
        notifier.expectChunk(CHODOV, ROZTYLY);
        notifier.expectChunk(ROZTYLY, KACEROV);
        notifier.expectChunk(KACEROV, BUDEJOVICKA);
        notifier.expectChunk(BUDEJOVICKA, PANKRAC);
        notifier.expectChunk(PANKRAC, PRAZSKEHO_POVSTANI);
        notifier.expectChunk(PRAZSKEHO_POVSTANI, VYSEHRAD);
        notifier.expectChunk(VYSEHRAD, IP_PAVLOVA);
        notifier.expectChunk(IP_PAVLOVA, MUZEUM);
        notifier.expectChunk(MUZEUM, HLAVNI_NADRAZI);
        notifier.expectChunk(HLAVNI_NADRAZI, FLORENC);
        notifier.expectChunk(FLORENC, VLTAVSKA);
        notifier.expectChunk(VLTAVSKA, NADRAZI_HOLESOVICE);
        notifier.expectChunk(NADRAZI_HOLESOVICE, KOBYLISY);
        notifier.expectChunk(KOBYLISY, LADVI);
        notifier.expectChunk(LADVI, STRIZKOV);
        notifier.expectChunk(STRIZKOV, PROSEK);
        notifier.expectChunk(PROSEK, LETNANY);
        notifier.expectChunk(LETNANY);

        notifier.expectChunk(PROSEK, STRIZKOV);
        notifier.expectChunk(STRIZKOV, LADVI);
        notifier.expectChunk(LADVI, KOBYLISY);
        notifier.expectChunk(KOBYLISY, NADRAZI_HOLESOVICE);
        notifier.expectChunk(NADRAZI_HOLESOVICE, VLTAVSKA);
        notifier.expectChunk(VLTAVSKA, FLORENC);
        notifier.expectChunk(FLORENC, HLAVNI_NADRAZI);
        notifier.expectChunk(HLAVNI_NADRAZI, MUZEUM);
        notifier.expectChunk(MUZEUM, IP_PAVLOVA);

        // TODO turnaround back to Florenc, prediction fails
        notifier.expectChunk(IP_PAVLOVA, VYSEHRAD);

        
        // line B
        notifier.expectChunk(FLORENC);
        notifier.expectChunk(NAMESTI_REPUBLIKY, MUSTEK);
        notifier.expectChunk(MUSTEK, NARODNI_TRIDA);
        notifier.expectChunk(NARODNI_TRIDA, KARLOVO_NAMESTI);
        notifier.expectChunk(KARLOVO_NAMESTI, ANDEL);
        notifier.expectChunk(ANDEL, SMICHOVSKE_NADRAZI);
        notifier.expectChunk(SMICHOVSKE_NADRAZI, RADLICKA);
        notifier.expectChunk(RADLICKA, JINONICE);
        notifier.expectChunk(JINONICE, NOVE_BUTOVICE);
        notifier.expectChunk(NOVE_BUTOVICE, HURKA);
        notifier.expectChunk(HURKA, LUZINY);
        notifier.expectChunk(LUZINY, LUKA);
        notifier.expectChunk(LUKA, STODULKY);
        notifier.expectChunk(STODULKY, ZLICIN);
        notifier.expectChunk(ZLICIN);
        notifier.expectChunk(STODULKY, LUKA);
        notifier.expectChunk(LUKA, LUZINY);
        notifier.expectChunk(LUZINY, HURKA);
        notifier.expectChunk(HURKA, NOVE_BUTOVICE);
        // Nove Butovice missing in source data
        // TODO prediction track lost
        notifier.expectChunk(JINONICE);
        notifier.expectChunk(RADLICKA, SMICHOVSKE_NADRAZI);
        notifier.expectChunk(SMICHOVSKE_NADRAZI, ANDEL);
        notifier.expectChunk(ANDEL, KARLOVO_NAMESTI);
        notifier.expectChunk(KARLOVO_NAMESTI, NARODNI_TRIDA);
        notifier.expectChunk(NARODNI_TRIDA, MUSTEK);
        notifier.expectChunk(MUSTEK, NAMESTI_REPUBLIKY);
        notifier.expectChunk(NAMESTI_REPUBLIKY, FLORENC);
        notifier.expectChunk(FLORENC, KRIZIKOVA);
        notifier.expectChunk(KRIZIKOVA, INVALIDOVNA);
        notifier.expectChunk(INVALIDOVNA, PALMOVKA);
        notifier.expectChunk(PALMOVKA, CESKOMORAVSKA);
        notifier.expectChunk(CESKOMORAVSKA, VYSOCANSKA);
        notifier.expectChunk(VYSOCANSKA, KOLBENOVA);
        notifier.expectChunk(KOLBENOVA, HLOUBETIN);
        notifier.expectChunk(HLOUBETIN, RAJSKA_ZAHRADA);
        // Rajska zahrada goes continuously into Cerny most
        notifier.onStation(RAJSKA_ZAHRADA);
        notifier.onStation(CERNY_MOST);
        notifier.expectChunk(RAJSKA_ZAHRADA, HLOUBETIN);
        notifier.expectChunk(HLOUBETIN, KOLBENOVA);
        notifier.expectChunk(KOLBENOVA, VYSOCANSKA);
        notifier.expectChunk(VYSOCANSKA, CESKOMORAVSKA);
        notifier.expectChunk(CESKOMORAVSKA, PALMOVKA);
        notifier.expectChunk(PALMOVKA, INVALIDOVNA);
        notifier.expectChunk(INVALIDOVNA, KRIZIKOVA);
        notifier.expectChunk(KRIZIKOVA, FLORENC);
        notifier.expectChunk(FLORENC, NAMESTI_REPUBLIKY);
        notifier.expectChunk(NAMESTI_REPUBLIKY, MUSTEK);
        notifier.expectChunk(MUSTEK);

        // line A
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
    public void testO2() throws Exception {
        replayFile("legacy/mslogs/o2.ms.log");

        // line C
        // journey begins heading to Haje
        notifier.startExpect();
        notifier.expectChunk(FLORENC);
        notifier.onStation(HLAVNI_NADRAZI); // missing disconnect in source data
        notifier.expectChunk(MUZEUM, IP_PAVLOVA);
        notifier.expectChunk(IP_PAVLOVA, VYSEHRAD);
        notifier.expectChunk(VYSEHRAD, PRAZSKEHO_POVSTANI);
        notifier.expectChunk(PRAZSKEHO_POVSTANI, PANKRAC);
        notifier.expectChunk(PANKRAC, BUDEJOVICKA);
        notifier.expectChunk(BUDEJOVICKA, KACEROV);
        notifier.expectChunk(KACEROV, ROZTYLY);
        notifier.expectChunk(ROZTYLY, CHODOV);
        notifier.expectChunk(CHODOV, OPATOV);
        notifier.expectChunk(OPATOV, HAJE);
        notifier.expectChunk(HAJE);
        notifier.expectChunk(OPATOV, CHODOV);
        notifier.expectChunk(CHODOV, ROZTYLY);
        notifier.expectChunk(ROZTYLY, KACEROV);
        notifier.expectChunk(KACEROV, BUDEJOVICKA);
        notifier.expectChunk(BUDEJOVICKA, PANKRAC);
        notifier.expectChunk(PANKRAC, PRAZSKEHO_POVSTANI);
        notifier.expectChunk(PRAZSKEHO_POVSTANI, VYSEHRAD);
        notifier.expectChunk(VYSEHRAD, IP_PAVLOVA);
        notifier.expectChunk(IP_PAVLOVA, MUZEUM);
        notifier.onStation(MUZEUM); // missing disconnect in source data
        notifier.expectChunk(HLAVNI_NADRAZI, FLORENC);
        notifier.expectChunk(FLORENC, VLTAVSKA);
        notifier.expectChunk(VLTAVSKA, NADRAZI_HOLESOVICE);
        notifier.expectChunk(NADRAZI_HOLESOVICE, KOBYLISY);
        notifier.expectChunk(KOBYLISY, LADVI);
        notifier.expectChunk(LADVI, STRIZKOV);
        notifier.expectChunk(STRIZKOV, PROSEK);
        notifier.expectChunk(PROSEK, LETNANY);
        notifier.expectChunk(LETNANY);

        notifier.expectChunk(PROSEK, STRIZKOV);
        notifier.expectChunk(STRIZKOV, LADVI);
        notifier.expectChunk(LADVI, KOBYLISY);
        notifier.expectChunk(KOBYLISY, NADRAZI_HOLESOVICE);
        notifier.expectChunk(NADRAZI_HOLESOVICE, VLTAVSKA);
        notifier.expectChunk(VLTAVSKA, FLORENC);
        notifier.expectChunk(FLORENC, HLAVNI_NADRAZI);
        notifier.onStation(HLAVNI_NADRAZI); // missing disconnect in source data

        // transfer to A -> Skalka
        notifier.expectChunk(MUZEUM);

        notifier.expectChunk(NAMESTI_MIRU, JIRIHO_Z_PODEBRAD);
        notifier.expectChunk(JIRIHO_Z_PODEBRAD, FLORA);
        notifier.expectChunk(FLORA, ZELIVSKEHO);
        notifier.expectChunk(ZELIVSKEHO, STRASNICKA);
        notifier.expectChunk(STRASNICKA, SKALKA);
        notifier.onStation(SKALKA);

        // pause
        notifier.onUnknownStation();

        // resume again on Florenc next day
        // line B
        notifier.expectChunk(FLORENC);
        notifier.expectChunk(NAMESTI_REPUBLIKY, MUSTEK);
        notifier.expectChunk(MUSTEK, NARODNI_TRIDA);
        notifier.expectChunk(NARODNI_TRIDA, KARLOVO_NAMESTI);
        notifier.expectChunk(KARLOVO_NAMESTI, ANDEL);
        notifier.expectChunk(ANDEL, SMICHOVSKE_NADRAZI);
        notifier.expectChunk(SMICHOVSKE_NADRAZI, RADLICKA);
        notifier.expectChunk(RADLICKA, JINONICE);
        notifier.expectChunk(JINONICE, NOVE_BUTOVICE);
        notifier.expectChunk(NOVE_BUTOVICE, HURKA);
        notifier.expectChunk(HURKA, LUZINY);
        // Luziny missing in source data
        // TODO prediction track lost
        notifier.expectChunk(LUKA);
        notifier.expectChunk(STODULKY, ZLICIN);
        notifier.expectChunk(ZLICIN);
        notifier.expectChunk(STODULKY, LUKA);
        notifier.expectChunk(LUKA, LUZINY);
        // TODO there seems to be mess in cellids around this place
        notifier.onStation(LUZINY); // missing disconnect in source data
        notifier.expectChunk(HURKA, NOVE_BUTOVICE);
        notifier.expectChunk(NOVE_BUTOVICE, JINONICE);
        notifier.expectChunk(JINONICE, RADLICKA);
        notifier.expectChunk(RADLICKA, SMICHOVSKE_NADRAZI);
        notifier.expectChunk(SMICHOVSKE_NADRAZI, ANDEL);
        notifier.expectChunk(ANDEL, KARLOVO_NAMESTI);
        notifier.expectChunk(KARLOVO_NAMESTI, NARODNI_TRIDA);
        notifier.expectChunk(NARODNI_TRIDA, MUSTEK);
        notifier.expectChunk(MUSTEK, NAMESTI_REPUBLIKY);
        notifier.expectChunk(NAMESTI_REPUBLIKY, FLORENC);
        notifier.expectChunk(FLORENC, KRIZIKOVA);
        notifier.expectChunk(KRIZIKOVA, INVALIDOVNA);
        notifier.expectChunk(INVALIDOVNA, PALMOVKA);
        notifier.expectChunk(PALMOVKA, CESKOMORAVSKA);
        notifier.expectChunk(CESKOMORAVSKA, VYSOCANSKA);
        notifier.expectChunk(VYSOCANSKA, KOLBENOVA);
        notifier.expectChunk(KOLBENOVA, HLOUBETIN);
        notifier.expectChunk(HLOUBETIN, RAJSKA_ZAHRADA);
        // Rajska zahrada goes continuously into Cerny most
        notifier.onStation(RAJSKA_ZAHRADA);
        notifier.onStation(CERNY_MOST);
        notifier.expectChunk(RAJSKA_ZAHRADA, HLOUBETIN);
        notifier.expectChunk(HLOUBETIN, KOLBENOVA);
        notifier.expectChunk(KOLBENOVA, VYSOCANSKA);
        notifier.expectChunk(VYSOCANSKA, CESKOMORAVSKA);
        notifier.expectChunk(CESKOMORAVSKA, PALMOVKA);
        notifier.expectChunk(PALMOVKA, INVALIDOVNA);
        notifier.expectChunk(INVALIDOVNA, KRIZIKOVA);
        notifier.expectChunk(KRIZIKOVA, FLORENC);
        notifier.expectChunk(FLORENC, NAMESTI_REPUBLIKY);
        notifier.expectChunk(NAMESTI_REPUBLIKY, MUSTEK);
        notifier.expectChunk(MUSTEK);

        // line A
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
