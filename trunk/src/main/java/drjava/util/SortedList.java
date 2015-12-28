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

public class SortedList<A> extends AbstractList<A> {
  private ArrayList<A> list = new ArrayList<A>();
  private Comparator<? super A> comparator;

  public SortedList(Comparator<? super A> comparator) {
    this.comparator = comparator;
  }

  public SortedList(Comparator<? super A> comparator, Collection<A> collection) {
    this.comparator = comparator;
    list.addAll(collection);
    Collections.sort(list, comparator);
  }

  public boolean add(A a) {
    int i = getInsertionIndex(a);
    list.add(i, a);
    return true;
  }

  public A get(int index) {
    return list.get(index);
  }

  public int size() {
    return list.size();
  }

  public A last() {
    return get(size()-1);
  }

  public A remove(int index) {
    return list.remove(index);
  }

  public int getInsertionIndex(A a) {
    int i = Collections.binarySearch(list, a, comparator);
    if (i < 0) i = -1-i;
    return i;
  }

  public int indexOf(Object a) {
    int i = Collections.binarySearch(list, (A) a, comparator);
    return i < 0 ? -1 : i;
  }
}