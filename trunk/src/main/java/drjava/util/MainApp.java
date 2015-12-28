/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import static drjava.util.ObjectUtil.toNiceString;
import static drjava.util.ObjectUtil.nice;

public class MainApp {
  private static Object mainApp;

  public static void exit() {
    System.exit(0);
  }

  public static synchronized boolean setMainApp(Object app) {
    if (mainApp == null) {
      Log.info("Registering main app " + nice(app));
      mainApp = app;
      return true;
    } else
      return false;
  }

  public static synchronized Object getMainApp() {
    return mainApp;
  }
}