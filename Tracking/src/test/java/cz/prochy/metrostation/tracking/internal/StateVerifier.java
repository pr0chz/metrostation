package cz.prochy.metrostation.tracking.internal;

import static org.easymock.EasyMock.*;

public class StateVerifier {

    private final Object [] mocks;

    public StateVerifier(Object... mocks) {
        this.mocks = mocks;
    }

    public void step(Runnable action, Runnable ... expects) {
        reset(mocks);
        for (Runnable expect : expects) {
            expect.run();
        }
        replay(mocks);
        action.run();
        verify(mocks);
    }

}
