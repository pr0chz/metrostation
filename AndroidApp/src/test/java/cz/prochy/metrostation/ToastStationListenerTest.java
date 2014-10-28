package cz.prochy.metrostation;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class ToastStationListenerTest {

	private Notifications notifications;
	private ToastStationListener listener;
	
	private final static String STATION = "testStation";
	
	@Before
	public void setUp() {
		notifications = createStrictMock(Notifications.class);
		listener = new ToastStationListener(notifications);
	}
	
	@Test
	public void testKnownStationCreatesMessage() {
		notifications.toastIncomingStation(eq(STATION));
		expectLastCall().once();
		replay(notifications);
		listener.onStation(STATION);
		verify(notifications);
	}
	
	@Test
	public void testRepeatedStationDoesNotCreateMessage() {
		
	}

	@Test
	public void testUnknownStationDoesNotCreateMessage() {
		
	}
	
	@Test
	public void testDisconnectFromKnownStationCreatesMessage() {
		
	}
	
	@Test
	public void testDisconnectFromUnknownStationDoesNotCreateMessage() {
		
	}

	@Test
	public void testDisconnectDoesCreateMessageIfThereWasPreviouslyKnownStation() {
		
	}
	
	@Test
	public void testDisconnectDoesNotNotifyTwice() {
		
	}
	
	@Test
	public void testAlreadyNotifiedKnownStationIsNotifiedAfterDisconnect() {
		
	}
	
}
