/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.util.ArrayList;
import java.util.List;

public class Tracing {
  private static ThreadLocal<List<Bus>> allBuses = new ThreadLocal<List<Bus>>();

  public static void busCreated(Bus bus) {
    if (allBuses.get() != null)
      allBuses.get().add(bus);
  }

  public static void startTracingBusCreations() {
    allBuses.set(new ArrayList<Bus>());
  }

  public static List<Bus> getAllBuses() {
    return new ArrayList<Bus>(allBuses.get());
  }

  public static void stopTracingBusCreations() {
    allBuses.set(null);
  }

  public static void tooComplicated(String text) {
    System.out.println("tooComplicated - " + text);
  }
}