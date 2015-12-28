/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class StreamUtil {
  public static String readFully(InputStream inputStream) throws IOException {
    DataInputStream dis = new DataInputStream(inputStream);
    StringBuffer buf = new StringBuffer();
    String line;
    try {
      while ((line = dis.readLine()) != null)
        buf.append(line).append("\n");
    } catch (IOException e) {
      // bugfix jazz 31.1.08
      if (!"Premature EOF".equals(e.getMessage()))
        throw e;
    }
    try {
      dis.close();
    } catch (IOException e) {
      // ignore
    }
    return buf.toString();
  }
}