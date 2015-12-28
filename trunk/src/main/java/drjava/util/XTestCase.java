/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

/*
This source file is part of Smyle, a database library.
For up-to-date information, see http://www.drjava.de/smyle
Copyright (C) 2001 Stefan Reich (doc@drjava.de)

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For full license text, see doc/license/lgpl.txt in this distribution
*/

package drjava.util;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.Iterator;

/** extends junit.framework.TestCase with a few useful methods */
public class XTestCase extends TestCase {
  private int rotateCounter = 0;
  
  public static void assertFalse(boolean b) {
    assertTrue(!b);
  }
  
  public static <A> void assertListEquals(Object[] expected, Collection<A> actual) {
    assertListEquals("", expected, actual);
  }
  
  public static <A> void assertListEquals(String msg, Object[] expected, Collection<A> actual) {
    if (msg.length() != 0) msg += ": ";
  //public static void assertListEquals(Object[] expected, List actual) {
    assertEquals(msg+"list size", expected.length, actual.size());
    Iterator<A> iter = actual.iterator();
    for (int i = 0; i < expected.length; i++)
      assertEquals(msg+"element #"+i, expected[i], iter.next());
  }

  public static void assertArrayEquals(Object[] expected, Object[] actual) {
    assertEquals("array size", expected.length, actual.length);
    for (int i = 0; i < expected.length; i++)
      assertEquals("element #"+i, expected[i], actual[i]);
  }

  protected void setUp() throws Exception {
    rotateCounter = 0;
  }
  
  protected final boolean rotate() {
    if (rotateCounter == 0) {
      rotateCounter = 1;
      return true;
    } else if (rotateCounter == 1) {
      doRotate();
      rotateCounter = 2;
      return true;
    } else {
      rotateCounter = 0;
      return false;
    }
  }
  
  protected boolean rotated() {
    return rotateCounter == 2;
  }
  
  protected void doRotate() {}  
}