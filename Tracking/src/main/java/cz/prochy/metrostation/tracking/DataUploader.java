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

    private static void sendRequest(byte [] loggerData) throws IOException{
        byte [] content = encodeToGzip(loggerData);
        URL url = new URL("http://46.101.221.156:48989/store");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty( "Content-Type", "text/plain");
        conn.setRequestProperty("Content-Encoding", "gzip");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString(content.length));
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
                stream.close();
            }
        }
        conn.getResponseCode();
        conn.disconnect();
    }

    public static void upload(String data) throws Exception {
        sendRequest(data.getBytes(Charset.forName("utf-8")));
    }

}
