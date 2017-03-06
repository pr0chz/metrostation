/*
 *     MetroStation
 *     Copyright (C) 2015, 2016, 2017 Jiri Pokorny
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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