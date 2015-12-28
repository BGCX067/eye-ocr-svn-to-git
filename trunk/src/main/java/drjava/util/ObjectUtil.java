/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class ObjectUtil {
  /** abbreviation of toNiceString */
  public static String nice(Object o) {
    return toNiceString(o);
  }

  /** U = unquoted (don't quote strings) */
  public static String toNiceStringU(Object o) {
    if (o instanceof String) return (String) o;
    return toNiceString(o);
  }

  public static String toNiceString(Object o) {
    if (o == null) return "null";

    // Nov 27 2007: this is new and could have weird effects in some places - watch out
    if (o instanceof String) return StringUtil.quote((String) o);

    if (o instanceof Class) return shortenClassName(((Class) o).getName());
    if (o instanceof Collection) return collectionToNiceString((Collection) o);
    if (o instanceof Point) return "(" + ((Point) o).x + "," + ((Point) o).y + ")";
    if (o instanceof InvocationTargetException)
      return toNiceString(((InvocationTargetException) o).getTargetException());

    String s;
    try {
      s = o.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return e.toString();
    }
    return niceify(o, s);
  }

  public static String niceify(Object o, String s) {
    String className = o.getClass().getName();
    if (s.startsWith(className + "@")) {
      s = className;
      s = shortenClassName(s);
    }
    String prefix = "java.lang.RuntimeException: ";
    while (o instanceof RuntimeException && s.startsWith(prefix))
      s = s.substring(prefix.length());
    return s;
  }

  public static String shortenClassName(String s) {
    int idx = s.lastIndexOf('.');
    if (idx >= 0) s = s.substring(idx+1);
    idx = s.lastIndexOf('$');
    if (idx >= 0 && !StringUtil.isInteger(s.substring(idx+1)))
      s = s.substring(idx+1);
    return s;
  }

  public static String beautifyClassName(String s) {
    String shortName = shortenClassName(s);
    int idx = s.lastIndexOf('.');
    if (idx >= 0) {
      String pkg = s.substring(0, idx);
      return shortName + " (" + pkg + ")";
    }
    return shortName;
  }

  private static String collectionToNiceString(Collection collection) {
    StringBuilder builder = new StringBuilder("(");
    boolean first = true;
    try {
      for (Object o : collection) {
        if (!first) builder.append(", ");
        builder.append(toNiceString(o));
        first = false;
      }
    } catch (Throwable e) {
      e.printStackTrace();
      builder.append(" [" + e + "]");
    }
    builder.append(")");
    return builder.toString();
  }

  public static boolean equal(Object a, Object b) {
    return a == null ? b == null : a.equals(b);
  }

  public static String showSize(Collection collection) {
    try {
      return collection == null ? "" : "[" + collection.size() + "]";
    } catch (Throwable e) {
      e.printStackTrace();
      return "[?]";
    }
  }

  public static <A> A cast(Object object, Class<A> targetClass) {
    return targetClass.cast(object);
  }
}