/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import prophecy.common.Trigger;

/** a thread-safe variable */
public class Var<A> extends Trigger {
  private A value;

  public Var() {
  }

  public Var(A value) {
    this.value = value;
  }

  public synchronized A get() {
    return value;
  }

  public synchronized void set(A value) {
    this.value = value;
    notifyAll();
    trigger();
  }

  public synchronized A swap(A value) {
    A a = this.value;
    set(value);
    return a;
  }

  public synchronized void waitForChange() {
    try {
      wait();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public synchronized void waitForChange(A knownValue) {
    if (knownValue == value)
      waitForChange();
  }
}