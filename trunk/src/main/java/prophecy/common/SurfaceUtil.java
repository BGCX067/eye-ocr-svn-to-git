/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import drjava.util.GUIUtil;
import static drjava.util.ObjectUtil.nice;

import javax.swing.*;

public class SurfaceUtil {
  public static void show(JComponent component) {
    show(nice(component), component);
  }
                                      
  public static void show(String title, JComponent component) {
    GUIUtil.showFrame(makeFrame(title, component));
  }

  public static JFrame makeFrame(String title, JComponent component) {
    JFrame frame = new JFrame(title);
    frame.setSize(800, 660);
    frame.getContentPane().add(component);
    return frame;
  }

  public static JFrame showAsMain(String title, JComponent component) {
    return GUIUtil.showMainFrame(makeFrame(title, component));
  }
}