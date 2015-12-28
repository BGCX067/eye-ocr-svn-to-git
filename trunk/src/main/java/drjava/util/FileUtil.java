/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.io.*;
import java.util.*;

public class FileUtil {
  public final static String charsetForTextFiles = "UTF8";

  /** writes safely (to temp file, then rename) */
  public static void saveTextFile(String fileName, String contents) throws IOException {
    File file = new File(fileName);
    File parentFile = file.getParentFile();
    if (parentFile != null)
      parentFile.mkdirs();
    String tempFileName = fileName + "_temp";
    FileOutputStream fileOutputStream = new FileOutputStream(tempFileName);
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, charsetForTextFiles);
    PrintWriter printWriter = new PrintWriter(outputStreamWriter);
    printWriter.print(contents);
    printWriter.close();
    if (file.exists() && !file.delete()) {
      Log.surprise("Can't delete " + fileName);
      return;
    }

    if (!new File(tempFileName).renameTo(file)) {
      Log.surprise("Can't rename " + tempFileName + " to " + fileName);
      return;
    }

    //Log.info("Wrote " + fileName);
  }

  public static String loadTextFile(String fileName) throws IOException {
    return loadTextFile(fileName, null);
  }

  public static String loadTextFile(String fileName, String defaultContents) throws IOException {
    if (!new File(fileName).exists())
      return defaultContents;

    FileInputStream fileInputStream = new FileInputStream(fileName);
    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charsetForTextFiles);
    return loadTextFile(inputStreamReader);
  }

  public static String loadTextFile(Reader reader) throws IOException {
    StringBuilder builder = new StringBuilder();
    try {
      BufferedReader bufferedReader = new BufferedReader(reader);
      String line;
      while ((line = bufferedReader.readLine()) != null)
        builder.append(line).append('\n');
    } finally {
      reader.close();
    }
    return builder.length() == 0 ? "" : builder.substring(0, builder.length()-1);
  }

  public static List<String> loadTextFileAsLines(String fileName) throws IOException {
    if (!new File(fileName).exists())
      return null;

    FileInputStream fileInputStream = new FileInputStream(fileName);
    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charsetForTextFiles);
    List<String> lines = new ArrayList<String>();
    try {
      BufferedReader reader = new BufferedReader(inputStreamReader);
      String line;
      while ((line = reader.readLine()) != null)
        lines.add(line);
    } finally {
      inputStreamReader.close();
    }
    return lines;
  }

  public static String loadTextFile(File file) throws IOException {
    return loadTextFile(file.getPath());
  }

  public static void saveTextFile(File file, String contents) throws IOException {
    saveTextFile(file.getPath(), contents);
  }

  public static void saveFile(File file, Object contents) throws IOException {
    if (contents instanceof String)
      saveTextFile(file, (String) contents);
    else
      throw new RuntimeException("Unknown contents type: " + contents);
  }
}
