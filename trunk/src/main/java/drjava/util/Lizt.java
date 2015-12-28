/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.util.*;

/** an immutable list */
public class Lizt<A> extends AbstractList<A> {
  private List<A> theList;

  public Lizt(A... items) {
    theList = new ArrayList<A>(Arrays.asList(items));
  }

  public Lizt(Collection<A> items) {
    theList = new ArrayList<A>(items);
  }

  public A get(int index) {
    return theList.get(index);
  }

  public int size() {
    return theList.size();
  }

  public static <A> Lizt<A> of(A... items) {
    return new Lizt<A>(items);
  }

  public static <A> Lizt<A> empty() {
    return new Lizt<A>();
  }

  public static <A> Lizt<A> repeat(A item, int count) {
    List<A> items = new ArrayList<A>(count);
    for (int i = 0; i < count; i++) items.add(item);
    return new Lizt<A>(items);
  }

  public Lizt<A> plus(A item) {
    List<A> items = new ArrayList<A>(this);
    items.add(item);
    return new Lizt<A>(items);
  }
}
