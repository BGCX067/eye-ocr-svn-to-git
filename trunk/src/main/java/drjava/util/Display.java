/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

public interface Display {
  Display put(Object o);
  Display nl();
  Display putnl(Object o);

  void beginSection(String section);
  void endSection();

  void indent();
  void unindent();

  /** ensure one blank line (can be called repeatedly and does not add more lines) */
  void vspace();

  void beginSubsection(String section);
  void endSubsection();
}