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

public class StreamDisplay implements Display {
  private boolean startOfLine = true;
  private int indent = 0;
  private boolean lastLineEmpty = true;
  private Writer writer;

  public StreamDisplay(Writer writer) {
    this.writer = writer;
  }

  protected StreamDisplay() {
  }

  protected void setWriter(Writer writer) {
    this.writer = writer;
  }

  public Display put(Object o) {
    try {
      if (startOfLine) {
        startOfLine = false;
        for (int i = 0; i < indent; i++) writer.write(" ");
      }
      doPut(o);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  private void doPut(Object o) {
    try {
      if (o == null)
        writer.write("null");
      else if (o instanceof Displayable)
        ((Displayable) o).display(this);
      else
        writer.write(ObjectUtil.toNiceStringU(o));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Display nl() {
    lastLineEmpty = startOfLine;
    try {
      writer.write("\r\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    startOfLine = true;
    return this;
  }

  public Display putnl(Object o) {
    return put(o).nl();
  }

  public void beginSection(String section) {
    nl();
    putnl("====== " + section + " ======");
    indent();
  }

  public void endSection() {
    vspace();
    unindent();
  }

  public void vspace() {
    if (!lastLineEmpty)
      nl();
  }

  public void beginSubsection(String section) {
    flushLine();
    putnl(section);
    indent();
  }

  public void endSubsection() {
    unindent();
  }

  private void flushLine() {
    if (!startOfLine)
      nl();
  }

  public void indent() {
    indent += 2;
  }

  public void unindent() {
    indent -= 2;
  }
}