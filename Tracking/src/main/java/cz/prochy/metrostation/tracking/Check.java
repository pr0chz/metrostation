package cz.prochy.metrostation.tracking;

public class Check {

	public static <T> T notNull(T o) {
		if (o == null) throw new NullPointerException();
		return o;
	}

}
