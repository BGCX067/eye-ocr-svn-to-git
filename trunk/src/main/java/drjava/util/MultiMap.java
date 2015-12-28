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

public class MultiMap<A,B> {
  private final Map<A, List<B>> data = new HashMap<A, List<B>>();

  public void put(A key, B value) {
    List<B> list = data.get(key);
    if (list == null)
      data.put(key, list = new ArrayList<B>());
    list.add(value);
  }

  public List<B> get(A key) {
    List<B> list = data.get(key);
    return list == null ? Collections.<B> emptyList() : list;
  }

  public Set<A> keySet() {
    return data.keySet();
  }
}