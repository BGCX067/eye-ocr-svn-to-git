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
import drjava.util.MainApp;
import drjava.util.ObjectUtil;
import prophecy.common.Prophecy;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application extends Trigger {
  private Flag retirementFlag = new Flag();
  private String description;

  public Application() {
  }

  public Application(String description) {
    this.description = description;
  }

  public String getStateDescription() {
    return isRetired() ? "closed" : "running";
  }

  public void retire() {
    retirementFlag.raise();
    trigger();
  }

  public boolean isRetired() {
    return retirementFlag.isUp();
  }

  public JFrame showFinalFrame(JFrame frame) {
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        retire();
      }
    });
    GUIUtil.showFrame(frame);
    return frame;
  }

  public void asMainApplication() {
    if (MainApp.setMainApp(this)) {
      addListener(new Runnable() {
        public void run() {
          if (isRetired())
            MainApp.exit();
        }
      });
    }
  }

  public String getDescription() {
    return description != null ? description : ObjectUtil.toNiceString(this);
  }

  /** override me */
  public void start() {
  }

  static {
    Prophecy.init();
  }
}
