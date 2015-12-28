/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common;

import prophecy.common.ClassData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class StdOutRecorder {
  public static void recordAndFileUnder(final Class mainClass) {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    tapSystemOutAndErr(baos);

    Runtime.getRuntime().addShutdownHook(new Thread("StdOutRecorder Shutdown Hook") {
      public void run() {
        String output = baos.toString();
        //System.out.println("Saving output on behalf of " + mainClass.getName());
        ClassData classData = ClassData.get(mainClass);
        classData.getClassPairing(StdOutRecorder.class).setString("lastOutput", output);
        classData.save();
      }
    });
  }

  public static void tapSystemOutAndErr(final OutputStream tapStream) {
    final PrintStream realOut = System.out;
    PrintStream out = new PrintStream(new OutputStream() {
      public void write(int b) throws IOException {
        realOut.write(b);
        //screen.print("" + (char) b);
        tapStream.write(b);
      }
    });
    System.setOut(out);
    final PrintStream realErr = System.err;
    PrintStream err = new PrintStream(new OutputStream() {
      public void write(int b) throws IOException {
        realErr.write(b);
        tapStream.write(b);
      }
    });
    System.setErr(err);
  }
}