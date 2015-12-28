/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

/** this class is fully thread-safe */
public class Flag implements Runnable {
  private boolean up;
  private Trigger listeners = new Trigger();

  /** returns true if flag was down before */
  public synchronized boolean raise() {
    boolean result = doRaise();
    if (result) {
      listeners.trigger();
      listeners = null;
    }
    return result;
  }

  private synchronized boolean doRaise() {
    if (!up) {
      up = true;
      notifyAll();
      return true;
    } else
      return false;
  }

  public synchronized void waitUntilUp() {
    if (!up) {
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void onRaise(Runnable listener) {
    if (!addListener(listener))
      listener.run();
  }

  private synchronized boolean addListener(Runnable listener) {
    if (!up) {
      listeners.addListener(listener);
      return true;
    } else
      return false;
  }

  public synchronized boolean isUp() {
    return up;
  }

  public String toString() {
    return isUp() ? "up" : "down";
  }

  public void run() {
    raise();
  }
}