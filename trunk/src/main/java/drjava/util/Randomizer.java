/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.util.List;

public abstract class Randomizer {
  /** @return a number between 0 and max (exclusive) */
  public abstract int getRandomNumber(int max);

  public double randomProb() {
    return getRandomNumber(50001)/50000.0;
  }

  /** getRandomNumber */
  public int random(int max) {
    return getRandomNumber(max);
  }

  public boolean oneIn(int n) {
    return random(n) == 0;
  }

  public <A> A oneOf(List<A> list) {
    return list.isEmpty() ? null : list.get(random(list.size()));
  }

  public int random(int min, int max) {
    return random(max-min)+min;
  }
}