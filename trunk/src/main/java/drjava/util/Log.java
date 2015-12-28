/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

public class Log {
  private static ThreadLocal<Logger> loggerMap = new ThreadLocal<Logger>();

  public static void surprise(String text) {
    getLogger().logSurprise(text);
  }

  public static void info(String text) {
    getLogger().log(text);
  }

  private static Logger getLogger() {
    Logger logger = loggerMap.get();
    if (logger == null)
      logger = new StdOutLogger();
    return logger;
  }
}
