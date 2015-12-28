/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.gui;

import prophecy.common.Prophecy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class PopDownButton extends JButton {
  public PopDownButton() {
    super(Prophecy.downIcon());
    setMargin(new Insets(0, 1, 0, 1)); // right=1 looks like 2 - strange
    setFocusable(false);
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JPopupMenu menu = new JPopupMenu();
        fillMenu(menu);
        menu.show(PopDownButton.this, 0, getHeight());
      }
    });
  }

  protected abstract void fillMenu(JPopupMenu menu);
}