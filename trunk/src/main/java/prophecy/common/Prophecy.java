/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import prophecy.common.gui.AutoVMExit;

import javax.swing.*;
import java.io.File;
import java.net.URL;

public class Prophecy {
  private static boolean inited;
  private static boolean initedHeadless;

  public static String dataDir() {
    return "data/";
  }

  public static String memoryDir() {
    return "memory/";
  }

  public static synchronized void init() {
    if (!inited) {
      inited = true;
      AutoVMExit.install();
      initUI();
    }
  }

  public static ImageIcon happyIcon() {
    return getIcon("happy.gif");
  }

  public static ImageIcon flatIcon() {
    return getIcon("flat.gif");
  }

  public static ImageIcon ghostIcon() {
    return getIcon("ghost.gif");
  }

  public static ImageIcon crazyIcon() {
    return getIcon("rcain.gif");
  }

  public static ImageIcon cupIcon() {
    return getIcon("cup_orange.gif");
  }

  public static Icon downIcon() {
    return getIcon("down.gif");
  }

  public static Icon closeIcon() {
    return getIcon("x.gif");
  }

  public static Icon refreshIcon() {
    return getIcon("refresh16.gif");
  }

  public static ImageIcon getIcon(String name) {
    String path = "img/" + name;
    if (new File(path).exists())
      return new ImageIcon(path);
    URL url = Prophecy.class.getClassLoader().getResource(path);
    if (url != null)
      return new ImageIcon(url);
    return null;
  }

  public static File getMemoryDir() {
    return new File("memory");
  }

  public static void initUI() {
    systemLookAndFeel();
  }

  public static void systemLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Throwable e) {
      //Errors.add(e);
    }
  }

  public static void disableAutoExit() {
    AutoVMExit.disable();
  }
}