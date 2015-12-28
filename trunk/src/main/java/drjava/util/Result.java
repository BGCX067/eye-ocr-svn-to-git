/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import static drjava.util.ObjectUtil.nice;

public class Result<A> {
  private A value;
  private boolean success;

  public Result(A value, boolean success) {
    this.success = success;
    this.value = value;
  }

  public Result(A value) {
    this.value = value;
    success = true;
  }

  public A get() {
    return value;
  }

  public A getValue() {
    return value;
  }

  public boolean isSuccess() {
    return success;
  }

  public String toString() {
    return nice(value);
  }

  public Throwable getException() {
    return null;
  }
}