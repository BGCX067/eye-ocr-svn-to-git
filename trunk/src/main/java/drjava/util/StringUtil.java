/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.util.regex.Pattern;
import java.util.*;
import static java.util.Arrays.asList;
import java.text.*;

public class StringUtil {
  public static String stripWhitespace(String s) {
    Pattern pattern = Pattern.compile("\\s");
    s = pattern.matcher(s).replaceAll("");
    return s;
  }

  public static String join(String glue, String[] strings) {
    return join(glue, asList(strings));
  }
  public static String join(String glue, Iterable<String> strings) {
    StringBuffer buf = new StringBuffer();
    Iterator<String> i = strings.iterator();
    if (i.hasNext()) {
      buf.append(i.next());
      while (i.hasNext())
        buf.append(glue).append(i.next());
    }
    return buf.toString();
  }

  public static String addSlash(String path) {
    return path.length() != 0 && !path.endsWith("/")? path + "/" : path;
  }

  public static List<String> toLinesWithoutWhitespace(byte[] bytes) {
    List<String> result = new ArrayList<String>();
    for (String s : toLines(bytes)) {
      String line = StringUtil.stripWhitespace(s);
      if (line.length() != 0)
        result.add(line);
    }
    return result;
  }

  public static List<String> toLines(byte[] rawContents) {
    return toLines(new String(rawContents));
  }

  public static List<String> toLines(String s) {
    List<String> lines = new ArrayList<String>();
    while (true) {
      int i = s.indexOf('\r'), j = s.indexOf('\n');
      if (j >= 0 && (j < i || i < 0)) i = j;
      if (i < 0) {
        if (s.length() != 0) lines.add(s);
        break;
      }

      lines.add(s.substring(0, i));
      if (s.charAt(i) == '\r' && i+1 < s.length() && s.charAt(i+1) == '\n')
        i += 2;
      else
        ++i;

      s = s.substring(i);
    }
    return lines;
  }

  public static byte[] fromLines(List<String> lines) {
    return stringFromLines(lines).getBytes();
  }

  public static String stringFromLines(List<String> lines) {
    StringBuffer buf = new StringBuffer();
    for (String line : lines) {
      buf.append(line).append('\n');
    }
    return buf.toString();
  }

  public static String replace(String pattern, String substitute, String s) {
    StringBuffer buf = new StringBuffer();
    while (true) {
      int i = s.indexOf(pattern);
      if (i >= 0) {
        buf.append(s.substring(0, i));
        buf.append(substitute);
        s = s.substring(i+pattern.length());
      } else {
        buf.append(s);
        break;
      }
    }
    return buf.toString();
  }

  public static String formatDouble(double d) {
    return /*Double.toString(d)*/formatDouble(d, 4);
  }

  public static String formatProbability(double probability) {
    int p = Math.min(99, (int) (probability * 100));
    return (p < 10 ? "0" : "") + p;
  }

  public static String formatProbability(double probability, int digits) {
    double p = probability*100;
    return formatDouble(p, digits);
  }

  public static String formatDouble(double d, int digits) {
    String format = "0.";
    for (int i = 0; i < digits; i++) format += "#";
    String s = new DecimalFormat(format).format(d);
    return s.replace(',', '.'); // hack german -> english
  }

  public static String maxLength(int maxLength, String s) {
    return s.length() > maxLength ? s.substring(0, maxLength) : s;
  }

  public static String escapeHtml(String text) {
    return text.replace("<", "&lt;").replace(">", "&gt;");
  }

  public static String ymd() {
    return new SimpleDateFormat("yyyyMMdd").format(new Date());
  }

  public static String firstToLower(String s) {
    return s.length() == 0 ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
  }

  public static String n(int count, String objectName) {
    return count + " " + (count == 1 ? objectName : plural(objectName));
  }

  private static String plural(String objectName) {
    return objectName.endsWith("y")
      ? objectName.substring(0, objectName.length()-1) + "ies"
      : objectName + "s";
  }

  public static boolean notEmpty(String s) {
    return s != null && s.length() != 0;
  }

  public static boolean isInteger(String s) {
    return Pattern.matches("\\-?\\d+", s);
  }

  public static void main(String[] args) {
    System.out.println(isInteger("-1"));
  }

  public static String formatTime(Date date) {
    return new SimpleDateFormat("HH:mm:ss").format(date);
  }

  public static String firstToUpper(String s) {
    return s.length() == 0 ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  public static String number(int i, String s) {
    return n(i, s);
  }

  public static String number(int count, String singular, String plural) {
    return n(count, singular, plural);
  }

  public static String n(int count, String singular, String plural) {
    return count + " " + (count == 1 ? singular : plural);
  }

  public static String indent(int i, String s) {
    String spaces = repeat(i, ' ');
    return spaces + s.replace("\n", "\n" + spaces);
  }

  private static String repeat(int len, char c) {
    char[] chars = new char[len];
    for (int i = 0; i < len; i++)
      chars[i] = c;
    return new String(chars);
  }

  public static String quoteAbbreviated(String text, int maxFront, int maxBack) {
    return abbreviate(Tree.quoteString(text), maxFront, maxBack);
  }

  public static String abbreviate(String text, int maxFront, int maxBack) {
    if (text.length() > maxFront + maxBack - 3) {
      text = text.substring(0, maxFront) + "..."
        + text.substring(text.length()-maxBack, text.length());
    }
    return text;
  }

  /** abbreviate to default length */
  public static String abbreviate(String text) {
    return abbreviate(text, 40, 20);
  }

  /** abbreviate to default length */
  public static String quoteAbbreviated(String text) {
    return quoteAbbreviated(text, 40, 20);
  }

  public static String stripCR(String text) {
    return text.replace("\r", "");
  }

  public static String quote(String text) {
    return Tree.quoteString(text);
  }

  public static String commonPrefix(String a, String b) {
    int i = 0;
    while (i < a.length() && i < b.length() && a.charAt(i) == b.charAt(i))
      ++i;
    return a.substring(0, i);
  }

  public static byte[] hexToBytes(String s) {
    byte[] bytes = new byte[s.length()/2];
    for (int i = 0; i < bytes.length; i++)
      bytes[i] = (byte) Integer.parseInt(s.substring(i*2, i*2+2), 16);
    return bytes;
  }

  public static String bytesToHex(byte[] bytes) {
    StringBuilder stringBuilder = new StringBuilder(bytes.length*2);
    for (int i = 0; i < bytes.length; i++) {
      String s = "0" + Integer.toHexString(bytes[i]);
      stringBuilder.append(s.substring(s.length()-2, s.length()));
    }
    return stringBuilder.toString();
  }

  public static String nlToBr(String s) {
    return s.replace("\n", "<br>");
  }
}