/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

public abstract class AbstractLogger implements Logger {
  public void logSurprise(String text) {
    log("Surprise: " + text);
  }

  public void logActivity(String text) {
    log(text);
  }

  public void logConfirmation(String text) {
    log("Confirmation: " + text);
  }

  public void logWarning(String text) {
    log("Warning: " + text);
  }

  public abstract void log(String text);
}