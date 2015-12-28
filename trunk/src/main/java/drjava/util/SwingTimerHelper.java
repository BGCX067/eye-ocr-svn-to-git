/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;



import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

/** Runs all timed code in Swing thread */
public class SwingTimerHelper implements ActionListener {
  private Runnable runnable;
  private Timer timer;
  private static final AtomicReference<Runnable> preRunHookUp = new AtomicReference<Runnable>();

  public SwingTimerHelper(final Updatable updatable, int delay) {
    this(new Runnable() {
      public void run() {
        updatable.update();
      }
    }, delay);
  }

  public SwingTimerHelper(Runnable runnable, int delay) {
    this.runnable = runnable;
    timer = new Timer(delay, this);
  }

  public void actionPerformed(ActionEvent evt) {
    /*SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          runnable.run();
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
    });*/
    try {
      Runnable hookUp = preRunHookUp.get();
      if (hookUp != null)
        hookUp.run();
      runnable.run();
    } catch (Throwable e) {
      Errors.add(e);
    }
  }

  public void installOn(final JComponent component) {
    if (component.isShowing())
      timer.start();

    component.addAncestorListener(new AncestorListener() {
      public void ancestorAdded(AncestorEvent event) {
        //Log.info("Starting SwingTimerHelper for " + component);
        timer.start();
      }

      public void ancestorRemoved(AncestorEvent event) {
        //Log.info("Stopping SwingTimerHelper for " + component);
        timer.stop();
      }

      public void ancestorMoved(AncestorEvent event) {
      }
    });
  }

  public void uninstall() {
    timer.stop();
  }

  public static void setPreRunHookUp(Runnable hookUp) {
    preRunHookUp.set(hookUp);
  }
}