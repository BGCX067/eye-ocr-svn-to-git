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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

public abstract class PopupMenuHelper extends MouseAdapter {
  public PopupMenuHelper() {
  }

  protected abstract void fillMenu(JPopupMenu menu, Point point);

  public void mousePressed(MouseEvent e) {
    displayMenu(e);
  }

  public void mouseReleased(MouseEvent e) {
    displayMenu(e);
  }

  private void displayMenu(MouseEvent e) {
    if (e.isPopupTrigger()) {
      JPopupMenu menu = new JPopupMenu();
      int count = menu.getComponentCount();
      fillMenu(menu, e.getPoint());
      if (menu.getComponentCount() != count)
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
  }

  public void install(JComponent component) {
    component.addMouseListener(this);
  }
}