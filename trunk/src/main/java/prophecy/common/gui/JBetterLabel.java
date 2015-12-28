/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.gui;

import drjava.util.PopupMenuHelper;
import drjava.util.StringUtil;
import drjava.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JBetterLabel extends JLabel {
  public JBetterLabel(String text) {
    setText(StringUtil.abbreviate(text));
    setToolTipText(text);

    new PopupMenuHelper() {
      protected void fillMenu(JPopupMenu menu, Point point) {
        JMenuItem miCopy = new JMenuItem("Copy text");
        miCopy.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SwingUtil.copyTextToClipboard(getText());
          }
        });
        menu.add(miCopy);
      }
    }.install(this);

    //setMaximumSize(new Dimension(1, 1));
  }

  /*
  public Dimension getMaximumSize() {
    Dimension size = super.getMaximumSize();
    System.out.println("maximumSize: " + size + ", text: " + getText());
    //size = new Dimension(300, size.height);
    return size;
  }

  public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    System.out.println("preferredSize: " + size + ", text: " + getText());
    //size = new Dimension(300, size.height);
    return size;
  }
  */
}