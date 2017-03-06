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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.GZIPOutputStream;

public class DataUploader {

    private static byte [] encodeToGzip(byte [] data) throws IOException {
        GZIPOutputStream gzip = null;
        try {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(byteArrayOS);
            gzip.write(data);
            gzip.flush();
            gzip.close();
            gzip = null;
            return byteArrayOS.toByteArray();
        } finally {
            if (gzip != null) {
                try { gzip.close(); } catch (Exception ignored) {}
            }
        }
    }

    private static void sendRequest(URL url, byte [] loggerData) throws IOException {
        byte [] content = encodeToGzip(loggerData);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("Content-Encoding", "gzip");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(content.length));
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(false);
        conn.connect();
        OutputStream stream = null;
        try {
            stream = conn.getOutputStream();
            stream.write(content);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {}
            }
            conn.getResponseCode();
            conn.disconnect();
        }
    }

    public static void upload(String url, String data) throws Exception {
        sendRequest(new URL(url), data.getBytes(Charset.forName("utf-8")));
    }

}
