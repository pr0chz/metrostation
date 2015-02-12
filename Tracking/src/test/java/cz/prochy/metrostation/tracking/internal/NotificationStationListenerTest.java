package cz.prochy.metrostation.tracking.internal;

import cz.prochy.metrostation.tracking.Notifications;
import cz.prochy.metrostation.tracking.Station;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

public class NotificationStationListenerTest {

    private final static Station STATION = new Station("12station232");
    private final static Station STATION2 = new Station("12station233");

    private Notifications notifications;
    private Timeout timeout;
    private NotificationStationListener listener;
    private StepVerifier verifier;

    @Before
    public void setUp() throws Exception {
        notifications = createStrictMock(Notifications.class);
        timeout = createStrictMock(Timeout.class);
        listener = new NotificationStationListener(notifications,timeout);
        verifier = new StepVerifier(notifications, timeout);
    }

    public void step(Runnable action, Runnable ... expects) {
        verifier.step(action, expects);
    }

    private Runnable station(Station station) {
        return () -> listener.onStation(station);
    }

    private Runnable unknownStation() {
        return () -> listener.onUnknownStation();
    }

    private Runnable disconnect() {
        return () -> listener.onDisconnect();
    }

    private Runnable expectArrivalNotification(Station station) {
        return () -> {
            notifications.notifyStationArrival(eq(station.getName()));
            expectLastCall().once();
        };
    }

    private Runnable expectDepartureNotification(Station station) {
        return () -> {
            notifications.notifyStationDeparture(eq(station.getName()));
            expectLastCall().once();
        };
    }

    private Runnable expectCancelTimeout() {
        return () -> {
            timeout.cancel();
            expectLastCall().once();
        };
    }

    private Runnable expectResetTimeout() {
        return () -> {
            timeout.reset(anyObject(Runnable.class));
            expectLastCall().once();
        };
    }

    private void stepStation(Station station) {
        step(station(station), expectCancelTimeout(), expectArrivalNotification(station));
    }

    @Test
    public void testStationIsNotified() throws Exception {
        stepStation(STATION);
    }

    @Test
    public void testSecondStationIsNotified() throws Exception {
        stepStation(STATION2);
    }

    @Test
    public void testUnknownStationResetsTimeout() throws Exception {
        stepStation(STATION);
        step(unknownStation(), expectResetTimeout());
    }

    @Test
    public void testSecondUnknownStationDoesNotResetTimer() throws Exception {
        stepStation(STATION);
        step(unknownStation(), expectResetTimeout());
        step(unknownStation());
    }

    @Test
    public void testDisconnectAfterStationCreatesNotification() throws Exception {
        stepStation(STATION);
        step(disconnect(), expectResetTimeout(), expectDepartureNotification(STATION));
    }

    @Test
    public void testSecondDisconnectIsIgnored() throws Exception {
        stepStation(STATION);
        step(disconnect(), expectResetTimeout(), expectDepartureNotification(STATION));
        step(disconnect());
    }

    @Test
    public void testDisconnectAfterUnknownStationIsIgnored() throws Exception {
        stepStation(STATION);
        step(unknownStation(), expectResetTimeout());
        step(disconnect());
    }

    @Test
    public void testUnknownAfterDisconnectIsIgnored() throws Exception {
        step(disconnect());
        step(unknownStation());
    }

    @Test
    public void testStationIsNotifiedAfterDisconnectsAndUnknown() throws Exception {
        stepStation(STATION);
        step(disconnect(), expectResetTimeout(), expectDepartureNotification(STATION));
        step(unknownStation());
        stepStation(STATION);
    }

    private Runnable catchTimerTask() {
        stepStation(STATION);

        reset(timeout);
        Capture<Runnable> runnable = new Capture<>(CaptureType.FIRST);
        timeout.reset(capture(runnable));
        expectLastCall().once();
        replay(timeout);

        listener.onUnknownStation();
        assertTrue("Capture failed, timeout reset was probably not called.", runnable.hasCaptured());
        return runnable.getValue();
    }

    @Test
    public void testTimerTaskRemovesNotification() throws Exception {
        Runnable task = catchTimerTask();

        reset(notifications);
        notifications.hideNotification();
        expectLastCall();
        replay(notifications);

        task.run();

        verify(notifications);
    }

}