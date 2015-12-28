/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import drjava.util.Attachments;
import drjava.util.Errors;

import static drjava.util.ObjectUtil.nice;

import java.util.ArrayList;
import java.util.List;

/** A queue of code fragments that are run one by one, typically in a dedicated thread.
 *  <p>
 *  Can be assigned to an arbitrary object as an attachment.
 *  */
public class CodeQueue {
  private List<Runnable> runnables = new ArrayList<Runnable>();
  private boolean stop;
  private int threadCount;
  private boolean autostart;

  /** add code to be executed - thread-safe */
  public void add(Runnable runnable) {
    //long startTime = System.currentTimeMillis();
    synchronized(this) {
      if (autostart && getThreadCount() == 0)
        startThread();
      runnables.add(runnable);
      notifyAll();
    }
    //long endTime = System.currentTimeMillis();
    //System.out.println("CodeQueue.add: " + (endTime-startTime) + " ms");
  }

  /** wait for runnables and execute them in an eternal loop */
  public void loop() {
    while (true) {
      runAll();
      try {
        synchronized(this) {
          wait();
        }
      } catch (InterruptedException e) {
        return;
      }
    }
  }

  public void runAll() {
    while (true) {
      Runnable runnable;
      synchronized (this) {
        if (stop) return;
        runnable = runnables.isEmpty() ? null : runnables.get(0);
        if (runnable != null)
          runnables.remove(0);
      }

      if (runnable != null) try {
        runnable.run();
      } catch (Throwable e) {
        Errors.add(e);
      } else
        return;
    }
  }

  /** get code queue assigned to object */
  public static CodeQueue get(Object object) {
    return Attachments.get(object, CodeQueue.class);
  }

  /** assign this code queue to an object (as an attachment) */
  public void attachTo(Object object) {
    Attachments.add(object, this);
  }

  /** can be called more than once, yielding multiple concurrent execution threads */
  public synchronized CodeQueue startThread() {
    ++threadCount;
    new Thread(nice(this)) {
      public void run() {
        try {
          loop();
        } finally {
          synchronized(CodeQueue.this) {
            --threadCount;
          }
        }
      }
    }.start();
    return this;
  }

  /** stops all execution threads. Also, no new threads can be started afterwards */
  public synchronized void stopThreads() {
    stop = true;
    notifyAll();
  }

  public synchronized int getThreadCount() {
    return threadCount;
  }

  public void setAutostart(boolean autostart) {
    this.autostart = autostart;
  }
}