/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ListUtil {
  public static <A> List<A> list(A... elements) {
    return Arrays.asList(elements);
  }

  public static <A> List<A> listWithoutNull(A... elements) {
    List<A> list = new ArrayList<A>();
    for (A a : elements) {
      if (a != null)
        list.add(a);
    }
    return list;
  }
}