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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Errors {
  private static boolean popup;
  private static List<ErrorListener> listeners = new ArrayList<ErrorListener>();

  public static void report(Throwable throwable) {
    add(throwable);
  }

  public static void add(final Throwable throwable) {
    throwable.printStackTrace();
    for (ErrorListener listener : listeners) {
      try {
        listener.errorReported(throwable);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    if (popup)
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          String text = throwable.toString();
          text = cutPrefix(text, "java.lang.RuntimeException: ");
          JOptionPane.showMessageDialog(null, text);
        }
      });
  }

  private static String cutPrefix(String text, String prefix) {
    if (text.startsWith(prefix) && text.length() > prefix.length())
      return text.substring(prefix.length());
    return text;
  }

  public static void setPopup(boolean popup) {
    Errors.popup = popup;
  }

  public static void addListener(ErrorListener listener) {
    listeners.add(listener);
  }

  public static String getStackTrace(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }

  /**
   * reports broken promise as an error
   */
  /*
  public static <A> Promise<A> check(Promise<A> promise) {
    promise.whenResolved(new PromiseAdapter<A>() {
      public void broken(Throwable throwable) {
        Errors.add(throwable);
      }
    });
    return promise;
  }
  */

  public static void throwAsRuntimeException(Throwable throwable) {
    throw asRuntimeException(throwable);
  }

  public static RuntimeException asRuntimeException(Throwable throwable) {
    if (throwable instanceof RuntimeException)
      return (RuntimeException) throwable;
    if (throwable == null)
      return new RuntimeException();
    return new RuntimeException(throwable);
  }
}