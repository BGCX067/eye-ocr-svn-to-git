/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.io.OutputStreamWriter;

public class StdOutDisplay extends StreamDisplay {
  private static StdOutDisplay instance = new StdOutDisplay();

  public StdOutDisplay() {
    super(new OutputStreamWriter(System.out));
  }

  public static StdOutDisplay get() {
    return instance;
  }
}