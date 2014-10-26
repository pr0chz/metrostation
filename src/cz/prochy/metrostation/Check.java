package cz.prochy.metrostation;

public class Check {

	public static <T> T notNull(T o) {
		if (o == null) throw new NullPointerException();
		return o;
	}

	public static <T> T notNull(T o, String message) {
		if (o == null) throw new NullPointerException(message);
		return o;
	}
	
}
