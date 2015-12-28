/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.gui;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;

import javax.swing.*;
import java.awt.*;

public class Form extends JPanel {
  public Form() {
    setLayout(LetterLayout.formWideRightSide().setSpacing(20, 5));
  }

  public Form(int minWidth) {
    this();
    GUIUtil.setMinimumWidth(this, minWidth);
  }

  public void addRow(String label, JComponent component) {
    add(makeLabel(label));
    add(component != null ? component : new JPanel());
  }

  public void addRow(JComponent component) {
    add(component);
    add(LetterLayout.getDummyComponent());
  }

  public void addSpacer() {
    addRow(makeLabel(" "));
  }

  private JLabel makeLabel(String text) {
    JLabel label = new JLabel(text);
    if (getFont() != null)
      label.setFont(getFont());
    return label;
  }

  public void addRow(String label, String text) {
    JBetterLabel label2 = new JBetterLabel(text);
    if (getFont() != null)
      label2.setFont(getFont());
    addRow(label, label2);
  }

  public void addHeading(String text) {
    addRow(makeLabel("\n" + text));
  }
}