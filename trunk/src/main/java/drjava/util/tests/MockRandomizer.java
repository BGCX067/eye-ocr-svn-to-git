/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util.tests;

import drjava.util.Randomizer;

import java.util.LinkedList;

public class MockRandomizer extends Randomizer {
  LinkedList<Integer> queue = new LinkedList<Integer>();

  public int getRandomNumber(int max) {
    if (queue.isEmpty())
      return 0;
    else {
      int i = queue.getFirst();
      queue.removeFirst();
      System.out.println("queue: " + i + ", max: " + max + " => " + (i % max));
      return i % max;
    }
  }

  public void setQueue(int... values) {
    queue.clear();
    for (int i : values)
      queue.add(i);
  }
}