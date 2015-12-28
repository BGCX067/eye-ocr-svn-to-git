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

public class Line extends JPanel {
  private JComponent filler;

  public Line(JComponent... components) {
    setLayout(LetterLayout.leftAlignedRow(10));
    for (JComponent component : components) {
      add(component);
    }
  }

  public void add(String text) {
    add(new JLabel(text));
  }

  public Line setBorder(int border) {
    ((LetterLayout) getLayout()).setBorder(border);
    return this;
  }

  public Line setSpacing(int spacing) {
    ((LetterLayout) getLayout()).setSpacing(spacing);
    return this;
  }

  public void addSpacer(int width) {
    JPanel panel = new JPanel();
    GUIUtil.setMinimumWidth(panel, width);
    add(panel);
  }

  public void setFiller(JComponent component) {
    if (filler != null)
      remove(filler);
    filler = component;
    add("Z", component);
    GUIUtil.revalidateAndRepaint(this);
  }
}