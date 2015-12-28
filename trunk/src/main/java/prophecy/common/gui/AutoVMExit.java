/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.gui;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class AutoVMExit extends TimerTask {
  private static boolean installed;
  private int lastWindowCount = -1;
  private static Timer timer;
  private static int firstDelay = 30000;
  private static int delay = 5000;

  public static void main(String[] args) throws Exception {
    install();
    runArgs(args);
  }

  public static void install() {
    if (!installed) {
      installed = true;
      timer = new Timer("AutoVMExit");
      timer.scheduleAtFixedRate(new AutoVMExit(), firstDelay, delay);
    }
  }

  private static void runArgs(String[] args) throws Exception {
    if (args.length == 0) return;
    String[] args2 = new String[args.length-1];
    System.arraycopy(args, 1, args2, 0, args2.length);
    Class.forName(args[0]).getMethod("main", args2.getClass()).invoke(null, (Object) args2);
  }

  public void run() {
    int windowCount = 0;
    Window[] windowList = Window.getWindows();
    for (Window window : windowList) {
      if (window.isVisible())
        ++windowCount;
    }
    //System.out.println("Windows: " + windowCount);
    if (windowCount == 0 && lastWindowCount == 0) {
      System.out.println("AutoVMExit: No windows open - exiting");
      System.exit(0);
    }
    lastWindowCount = windowCount;
  }

  public static void disable() {
    timer.cancel();
  }

  public static int getFirstDelay() {
    return firstDelay;
  }

  public static void setFirstDelay(int firstDelay) {
    AutoVMExit.firstDelay = firstDelay;
  }

  public static int getDelay() {
    return delay;
  }

  public static void setDelay(int delay) {
    AutoVMExit.delay = delay;
  }
}