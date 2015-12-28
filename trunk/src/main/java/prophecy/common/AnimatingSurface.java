/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import drjava.util.SwingTimerHelper;


/**
 * Demos that animate extend this class.
 */
public abstract class AnimatingSurface extends Surface implements Runnable {

    public Thread thread;
  private final int delay = 200;

  public abstract void step(int w, int h);

    public abstract void reset(int newwidth, int newheight);


    public void start() {
        if (thread == null && !dontThread) {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setName(name + " Demo");
            thread.start();
        }
    }


    public synchronized void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = null;
        notifyAll();
    }


    public void run() {

        Thread me = Thread.currentThread();

        while (thread == me && !isShowing() || getSize().width == 0) {
            try {
                thread.sleep(delay);
            } catch (InterruptedException e) { }
        }

        while (thread == me) {
            repaint();
            try {
                thread.sleep(sleepAmount);
            } catch (InterruptedException e) { }
        }
        thread = null;
    }

    public void startWhenVisible() {
      new SwingTimerHelper(new Runnable() {
        public void run() {
          repaint();
        }
      }, delay).installOn(this);
    }
}