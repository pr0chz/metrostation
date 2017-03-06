/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.prochy.metrostation.tracking;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Analyzer {

    public static class TestRecord {
        public final int cid;
        public final int lac;
        public final long ts;

        public TestRecord(long ts, int cid, int lac) {
            this.ts = ts;
            this.cid = cid;
            this.lac = lac;
        }

        public static final int DISCONNECT_CID = -2;

        @Override
        public String toString() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            if (cid != DISCONNECT_CID) {
                return format.format(new Date(ts)) + ": {ts: " + ts + ", cid: " + cid + ", lac: " + lac + "}";
            } else {
                return format.format(new Date(ts)) + ": {ts: " + ts + "}";
            }
        }
    }

    public static List<TestRecord> readMsLogsLines(InputStream stream) {
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

}
