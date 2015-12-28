/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

public abstract class Rotation {
  private int rotateCounter = 0;
  
  public final boolean rotate() {
    if (rotateCounter == 0) {
      rotateCounter = 1;
      return true;
    } else if (rotateCounter == 1) {
      doRotate();
      rotateCounter = 2;
      return true;
    } else {
      rotateCounter = 0;
      return false;
    }
  }
  
  public boolean rotated() {
    return rotateCounter == 2;
  }
  
  protected abstract void doRotate();
}