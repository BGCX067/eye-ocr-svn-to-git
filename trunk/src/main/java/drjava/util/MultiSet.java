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

public class MultiSet<A> implements FromTree, ToTree {
  private Map<A, Integer> map = new HashMap<A, Integer>();

  public void add(A key) {
    add(key, 1);
  }

  public void add(A key, int count) {
    if (map.containsKey(key))
      map.put(key, map.get(key)+count);
    else
      map.put(key, count);
  }

  public int get(A key) {
    return map.containsKey(key) ? map.get(key) : 0;
  }

  public void remove(A key) {
    Integer i = map.get(key);
    if (i != null && i > 1)
      map.put(key, i - 1);
    else
      map.remove(key);
  }

  public List<A> getTopTen(int maxSize) {
    List<A> list = getSortedListDescending();
    return list.size() > maxSize ? list.subList(0, maxSize) : list;
  }

  public List<A> getSortedListDescending() {
    List<A> list = new ArrayList<A>(map.keySet());
    Collections.sort(list, new Comparator<A>() {
      public int compare(A a, A b) {
        return map.get(b).compareTo(map.get(a));
      }
    });
    return list;
  }

  public int getNumberOfUniqueElements() {
    return map.size();
  }

  public Set<A> asSet() {
    return map.keySet();
  }

  public A getMostPopularEntry() {
    int max = 0;
    A a = null;
    for (Map.Entry<A,Integer> entry : map.entrySet()) {
      if (entry.getValue() > max) {
        max = entry.getValue();
        a = entry.getKey();
      }
    }
    return a;
  }

  public void removeAll(A key) {
    map.remove(key);
  }

  public A getRandomEntry(Randomizer rand) {
    int total = size(), n = rand.getRandomNumber(total);

    for (Map.Entry<A,Integer> entry : map.entrySet()) {
      n -= entry.getValue();
      if (n < 0)
        return entry.getKey();
    }

    throw new RuntimeException("huch");
  }

  public int size() {
    int size = 0;
    for (int i : map.values())
      size += i;
    return size;
  }

  public MultiSet<A> mergeWith(MultiSet<A> set) {
    MultiSet<A> result = new MultiSet<A>();
    for (A a : set.asSet()) {
      result.add(a, set.get(a));
    }
    return result;
  }

  public Tree toTree() {
    Tree tree = new Tree(getClass());
    for (A a : asSet()) {
      tree.add(
        new Tree(get(a)).add(TreeUtil.objectToTree(a)));
    }
    return tree;
  }

  public void fromTree(Tree tree) {
    for (Tree k : tree.arguments()) {
      add((A) TreeUtil.treeToObject(k.get(0)), k.intValue());
    }
  }

  public String toString() {
    StringBuilder builder = new StringBuilder("{");
    boolean first = true;
    for (A a : asSet()) {
      if (first)
        first = false;
      else
        builder.append(", ");
      builder.append(a);
      int count = get(a);
      if (count != 1) builder.append("*").append(StringUtil.formatDouble(count));
    }
    builder.append("}");
    return builder.toString();
  }
}