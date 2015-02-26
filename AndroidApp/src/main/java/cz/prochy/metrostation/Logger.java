package cz.prochy.metrostation;

import android.os.Environment;

import java.io.*;

public class Logger {

    private final static File logFile = new File(Environment.getExternalStorageDirectory(), "ms.log");

    private static synchronized void doLog(String message) {
        try {
            OutputStream str = null;
            try {
                str = new FileOutputStream(logFile, true);
                OutputStreamWriter writer = new OutputStreamWriter(str);
                writer.write(message);
                writer.close();
            } finally {
                str.close();
            }
        } catch (IOException e) {
            // nothing to do
        }
    }

    public void log(String message) {
        doLog(message);
    }

    public void log(Throwable e) {
        log(getStackTrace(e));
    }

    private static String getStackTrace(Throwable aThrowable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        printWriter.write(aThrowable.toString() + ":\n");
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

}
