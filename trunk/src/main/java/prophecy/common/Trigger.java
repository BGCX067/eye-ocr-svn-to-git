/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import drjava.util.Errors;

import java.util.ArrayList;

public class Trigger implements RunnableListenerList {
  private ArrayList<Runnable> listeners = new ArrayList<Runnable>();

  public void trigger() {
    synchronized(this) {
      notifyAll();
    }

    for (Runnable listener : listeners) {
      try {
        listener.run();
      } catch (Throwable e) {
        Errors.add(e);
      }
    }
  }

  public void addListener(Runnable listener) {
    listeners.add(listener);
  }

  public void removeListener(Runnable listener) {
    listeners.remove(listener);
  }

  public synchronized void waitUntilTriggered() {
    try {
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}