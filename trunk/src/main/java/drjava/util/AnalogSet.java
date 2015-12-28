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

public class AnalogSet<A> {
  private Map<A, Double> map = new HashMap<A, Double>();
  private boolean allowZero;

  public AnalogSet() {
  }

  public AnalogSet(Collection<A> collection) {
    for (A a : collection) {
      add(a, 1.0);
    }
  }

  public double containsAll(AnalogSet<A> set) {
    if (set.isEmpty()) return 1;
    double result = 0.0;
    for (A a : set.asSet()) {
      result += set.get(a) * get(a);
    }
    //System.out.println("containsAll " + this + " " + set + " = " + result);
    return result / /*set.getTotalWeight()*/ set.size();
  }

  private int size() {
    return map.size();
  }

  public double getTotalWeight() {
    double result = 0.0;
    for (Double weight : map.values()) result += weight;
    return result;
  }

  public Set<A> asSet() {
    return map.keySet();
  }

  public double get(A a) {
    Double weight = map.get(a);
    return weight == null ? 0.0 : weight;
  }

  public void add(A a, double weight) {
    double newWeight = get(a) + weight;
    if (newWeight != 0.0 || allowZero) {
      map.put(a, newWeight);
    } else
      map.remove(a);
  }

  public static <A> AnalogSet<A> singleton(A a, double weight) {
    AnalogSet<A> set = new AnalogSet<A>();
    set.add(a, weight);
    return set;
  }

  public static <A> AnalogSet<A> emptySet() {
    return new AnalogSet<A>();
  }

  public boolean isEmpty() {
    return map.isEmpty();
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
      double weight = get(a);
      if (weight != 1.0) builder.append("*").append(StringUtil.formatDouble(weight));
    }
    builder.append("}");
    return builder.toString();
  }

  public List<A> sortedByDescValue() {
    List<A> list = new ArrayList<A>(asSet());
    Collections.sort(list, new Comparator<A>() {
      public int compare(A a, A b) {
        double w1 = get(a), w2 = get(b);
        return w1 < w2 ? 1 : w1 > w2 ? -1 : 0;
      }
    });
    return list;
  }

  public void addAll(AnalogSet<A> set) {
    for (A a : set.asSet()) {
      add(a, set.get(a));
    }
  }

  public boolean isAllowZero() {
    return allowZero;
  }

  public void setAllowZero(boolean allowZero) {
    this.allowZero = allowZero;
  }

  public Tree toTree() {
    Tree tree = new Tree(getClass());
    for (A a : asSet()) {
      tree.add(
        new Tree().add(new Tree(get(a))).add(TreeUtil.objectToTree(a)));
    }
    return tree;
  }

  public void fromTree(Tree tree) {
    for (Tree k : tree.arguments()) {
      add((A) TreeUtil.treeToObject(k.get(1)), k.get(0).doubleValue());
    }
  }

  public void remove(A a) {
    map.remove(a);
  }

  public boolean contains(A a) {
    return map.containsKey(a);
  }
}