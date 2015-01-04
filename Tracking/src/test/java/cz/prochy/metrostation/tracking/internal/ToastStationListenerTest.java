package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Notifications;
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


    private void expectArrivalToast(String station) {
        notifications.toastStationArrival(eq(station));
        expectLastCall();
    }

    private void expectDepartureToast(String station) {
        notifications.toastStationDeparture(eq(station));
        expectLastCall();
    }

    @Test
    public void testNotificationTriggeredOnFirstStation() throws Exception {
        expectArrivalToast(STATION);
        replay(notifications);
        toast.onStation(STATION);
        verify(notifications);
    }

    @Test
    public void testNotificationTriggeredOnSameStationAfterUnknown() throws Exception {
        expectArrivalToast(STATION);
        expectArrivalToast(STATION);
        replay(notifications);
        toast.onStation(STATION);
        toast.onUnknownStation();
        toast.onStation(STATION);
        verify(notifications);
    }

    @Test
    public void testNotificationTriggeredOnSameStationAfterDisconnect() throws Exception {
        expectArrivalToast(STATION);
        expectDepartureToast(STATION);
        expectArrivalToast(STATION);
        replay(notifications);
        toast.onStation(STATION);
        toast.onDisconnect();
        toast.onStation(STATION);
        verify(notifications);
    }


    @Test
    public void testNotificationTriggeredOnDifferentStation() throws Exception {
        expectArrivalToast(STATION);
        expectArrivalToast(STATION2);
        replay(notifications);
        toast.onStation(STATION);
        toast.onStation(STATION2);
        verify(notifications);
    }

    @Test
    public void testNotificationTriggeredOnDisconnectAfterSecondStation() throws Exception {
        expectArrivalToast(STATION);
        expectArrivalToast(STATION2);
        expectDepartureToast(STATION2);
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