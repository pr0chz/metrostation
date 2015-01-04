package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Notifications;
import cz.prochy.metrostation.tracking.internal.ToastStationListener;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class ToastStationListenerTest {

    private final static String STATION = "12station12";
    private final static String STATION2 = "12station12_2";

    private Notifications notifications;
    private ToastStationListener toast;

    @Before
    public void setUp() throws Exception {
        notifications = createStrictMock(Notifications.class);
        toast = new ToastStationListener(notifications);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsOnNullNotifications() throws Exception {
        new ToastStationListener(null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsOnNullStation() throws Exception {
        toast.onStation(null);
    }


    private void expectIncomingToast(String station) {
        notifications.toastIncomingStation(eq(station));
        expectLastCall();
    }

    private void expectLeavingToast(String station) {
        notifications.toastLeavingStation(eq(station));
        expectLastCall();
    }

    @Test
    public void testNotificationTriggeredOnFirstStation() throws Exception {
        expectIncomingToast(STATION);
        replay(notifications);
        toast.onStation(STATION);
        verify(notifications);
    }

    @Test
    public void testNotificationTriggeredOnSameStationAfterUnknown() throws Exception {
        expectIncomingToast(STATION);
        expectIncomingToast(STATION);
        replay(notifications);
        toast.onStation(STATION);
        toast.onUnknownStation();
        toast.onStation(STATION);
        verify(notifications);
    }

    @Test
    public void testNotificationTriggeredOnSameStationAfterDisconnect() throws Exception {
        expectIncomingToast(STATION);
        expectLeavingToast(STATION);
        expectIncomingToast(STATION);
        replay(notifications);
        toast.onStation(STATION);
        toast.onDisconnect();
        toast.onStation(STATION);
        verify(notifications);
    }


    @Test
    public void testNotificationTriggeredOnDifferentStation() throws Exception {
        expectIncomingToast(STATION);
        expectIncomingToast(STATION2);
        replay(notifications);
        toast.onStation(STATION);
        toast.onStation(STATION2);
        verify(notifications);
    }

    @Test
    public void testNotificationTriggeredOnDisconnectAfterSecondStation() throws Exception {
        expectIncomingToast(STATION);
        expectIncomingToast(STATION2);
        expectLeavingToast(STATION2);
        replay(notifications);
        toast.onStation(STATION);
        toast.onStation(STATION2);
        toast.onDisconnect();
        verify(notifications);
    }


    @Test
    public void testNotificationNotTriggeredOnDisconnectWithUnknownStation() throws Exception {
        replay(notifications);
        toast.onDisconnect();
        verify(notifications);
    }

}