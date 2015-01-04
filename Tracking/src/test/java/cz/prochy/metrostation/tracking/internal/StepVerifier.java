package cz.prochy.metrostation.tracking.internal;

import java.util.stream.Stream;

import static org.easymock.EasyMock.*;

public class StepVerifier {

    private final Object [] mocks;

    public StepVerifier(Object... mocks) {
        this.mocks = mocks;
    }

    public void step(Runnable action, Runnable ... expects) {
        reset(mocks);
        Stream.of(expects).forEach(Runnable::run);
        replay(mocks);
        action.run();
        verify(mocks);
    }

}
