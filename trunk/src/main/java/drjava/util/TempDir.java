/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.io.File;

public class TempDir {
  private static int counter = (int) System.currentTimeMillis();

  public static File createNew() {
    File dir = new File("temp/"+(counter++));
    dir.mkdir();
    return dir;
  }
}