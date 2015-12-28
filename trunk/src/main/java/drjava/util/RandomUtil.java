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

public class RandomUtil {
  private static Randomizer stdRandomizer = new RealRandomizer();

  public static <A> A oneOf(List<A> list) {
    return stdRandomizer.oneOf(list);
  }

  public static boolean chance(double probability) {
    return stdRandomizer.randomProb() <= probability;
  }
}