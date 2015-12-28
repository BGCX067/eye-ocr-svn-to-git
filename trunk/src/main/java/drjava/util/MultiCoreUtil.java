/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.lang.management.ManagementFactory;
import java.util.*;

public class MultiCoreUtil {
  private static int numCores = getNumberOfProcessors();

  /** This incurs some overhead if the list is very long. Better make it fewer pieces */
  public static <A, B> List<B> parallelMap(Collection<A> collection, final Function<A, B> f) {
    final List<B> result = Collections.synchronizedList(new ArrayList<B>());

    if (numCores == 1)
      for (A a : collection) {
        result.add(f.get(a));
      }
    else {
      // synchronizedList shuld not be needed here - it's just reading access to an array
      //final List<A> list = Collections.synchronizedList(new ArrayList<A>(collection));
      final ArrayList<A> list = new ArrayList<A>(collection);

      for (int i = 0; i < list.size(); i++)
        result.add(null);

      Thread thread2 = new Thread("core 2") {
        public void run() {
          for (int i = 1; i < list.size(); i += 2)
            result.set(i, f.get(list.get(i)));
        }
      };
      /* TODO thread2.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread t, Throwable e) {
        }
      });*/
      thread2.start();

      for (int i = 0; i < list.size(); i += 2)
        result.set(i, f.get(list.get(i)));

      try {
        thread2.join();
      } catch (InterruptedException e) {}
    }

    return result;
  }

  public static int getNumCores() {
    return numCores;
  }

  public static void setNumCores(int numCores) {
    MultiCoreUtil.numCores = numCores;
  }

  public static void main(String[] args) {
    System.out.println("Processors: " + getNumberOfProcessors());
    setNumCores(2);
    List<Object> collection = Arrays.asList(null, null);
    long t1 = System.currentTimeMillis();
    parallelMap(collection, new Function<Object, Object>() {
      public Object get(Object o) {
          long t1 = System.currentTimeMillis();
          while (System.currentTimeMillis()-t1 < 5000) {}
        /*try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
        }*/
        return null;
      }
    });
    long t2 = System.currentTimeMillis();
    System.out.println(t2-t1);
  }

  public static int getNumberOfProcessors() {
    return ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
  }

  /** wie Thread.sleep aber ohne die bekackte Exception */
  public static void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void sleepForever() {
    Object object = new Object();
    synchronized(object) {
      try {
        object.wait();
      } catch(InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /** for test purposes */
  public static <A, B> List<B> serialMap(Collection<A> collection, final Function<A, B> f) {
    final List<B> result = new ArrayList<B>();
    for (A a : collection)
      result.add(f.get(a));
    return result;
  }
}