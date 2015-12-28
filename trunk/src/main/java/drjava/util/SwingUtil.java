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
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

public class SwingUtil {
  public static void centerInOwner(Dialog dialog) {
    Container parent = dialog.getOwner();
    if (parent != null  && parent.isShowing())
      centerInParent(dialog, dialog.getParent());
    else
      center(dialog);
  }

  public static void centerInParent(Window window, Component myParent) {
    int x;
    int y;

    Point topLeft = myParent.getLocationOnScreen();
    Dimension parentSize = myParent.getSize();
    Dimension mySize = window.getSize();
    if (parentSize.width > mySize.width)
      x = ((parentSize.width - mySize.width)/2) + topLeft.x;
    else
      x = topLeft.x;

    if (parentSize.height > mySize.height)
      y = ((parentSize.height - mySize.height)/2) + topLeft.y;
    else
      y = topLeft.y;

    window.setLocation(x, y);
  }

  public static void focusOnOpen(final JComponent component) {
    /*
    final Window window = SwingUtilities.getWindowAncestor(component);
    if (window != null)
      window.addWindowListener(new WindowAdapter() {
        public void windowOpened(WindowEvent e) {
          component.requestFocus();
          window.removeWindowListener(this);
        }
      });
    */

    component.addAncestorListener(new AncestorListener() {
      public void ancestorAdded(AncestorEvent event) {
        component.requestFocusInWindow();
        component.removeAncestorListener(this);
      }

      public void ancestorRemoved(AncestorEvent event) {
      }

      public void ancestorMoved(AncestorEvent event) {
      }
    });
  }

  public static void center(Window window) {
    Dimension screen = window.getToolkit().getScreenSize();
    window.setLocation((screen.width-window.getWidth())/2, (screen.height-window.getHeight())/2);
  }

  public static void setPopupMenu(final JTable table, final PopupMenuFactory factory) {
    table.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        mouseReleased(e);
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          int row = table.rowAtPoint(e.getPoint());
          if (!table.isRowSelected(row))
            table.setRowSelectionInterval(row, row);

          JPopupMenu popup = factory.createPopupMenu();
          if (popup != null)
            popup.show(e.getComponent(), e.getX() + 2, e.getY());
        }
      }
    });
  }

  public static void setPopupMenu(final JList list, final PopupMenuFactory factory) {
    list.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        mouseReleased(e);
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          int row = list.locationToIndex(e.getPoint());
          if (!list.isSelectedIndex(row))
            list.setSelectedIndex(row);

          JPopupMenu popup = factory.createPopupMenu();
          if (popup != null)
            popup.show(e.getComponent(), e.getX() + 2, e.getY());
        }
      }
    });
  }

  public static void setPopupMenu(final JComponent c, final PopupMenuFactory factory) {
    c.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        mouseReleased(e);
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          JPopupMenu popup = factory.createPopupMenu();
          if (popup != null)
            popup.show(e.getComponent(), e.getX() + 2, e.getY());
        }
      }
    });
  }

  public static void copyTextToClipboard(String data) {
    StringSelection selection = new StringSelection(data);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
  }

  public static void disposeParentWindow(JComponent component) {
    Window window = SwingUtilities.windowForComponent(component);
    if (window != null)
      window.dispose();
  }
}