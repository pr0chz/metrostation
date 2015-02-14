package cz.prochy.metrostation.tracking;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class Check {

    public static <T> T notNull(T o) {
        if (o == null) throw new NullPointerException();
        return o;
    }

}
