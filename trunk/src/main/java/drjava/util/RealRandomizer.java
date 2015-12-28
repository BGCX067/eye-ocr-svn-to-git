/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.util.Random;

public class RealRandomizer extends Randomizer {
  private Random random = new Random();

  public int getRandomNumber(int max) {
    // TODO: make new Random every once in a while
    return random.nextInt(max);
    //return new Random().nextInt(max);
  }
}