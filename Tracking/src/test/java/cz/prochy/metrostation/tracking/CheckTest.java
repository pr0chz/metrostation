package cz.prochy.metrostation.tracking;

import org.junit.Test;

import static org.junit.Assert.assertSame;

public class CheckTest {

    @Test(expected = NullPointerException.class)
    public void testThrowsOnNull() throws Exception {
        Check.notNull(null);
    }

    @Test
    public void testReturnsIdentityOnProperObject() throws Exception {
        Object o = new Object();
        assertSame(o, Check.notNull(o));
    }


}