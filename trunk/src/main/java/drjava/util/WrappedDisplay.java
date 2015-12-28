/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

/** a Display implementation producing a line-wrapped string
 *  with a (more or less) fixed line width */
public class WrappedDisplay implements Display {
  private int lineWidth = 80;
  private int col = 0;
  private StringBuilder stringBuilder = new StringBuilder();

  public WrappedDisplay() {
  }

  public Display put(Object o) {
    if (o == null)
      printWord("null");
    else if (o instanceof Displayable)
      ((Displayable) o).display(this);
    else
      printWord(ObjectUtil.toNiceStringU(o));
    return this;
  }

  private void printWord(String word) {
    if (col + word.length() > lineWidth && col != 0) {
      realNewLine();
    }
    stringBuilder.append(word);
    col += word.length();
  }

  private void realNewLine() {
    stringBuilder.append("\r\n");
    col = 0;
  }

  public Display nl() {
    printWord(" ");
    return this;
  }

  /** ignore the nl */
  public Display putnl(Object o) {
    return put(o).put(" ");
  }

  public void beginSection(String section) {
    throw new RuntimeException("unimplemented");
  }

  public void endSection() {
    throw new RuntimeException("unimplemented");
  }

  public void indent() {
  }

  public void unindent() {
  }

  public void vspace() {
  }

  public void beginSubsection(String section) {
    throw new RuntimeException("unimplemented");
  }

  public void endSubsection() {
    throw new RuntimeException("unimplemented");
  }

  public String toString() {
    return stringBuilder.toString();
  }
}