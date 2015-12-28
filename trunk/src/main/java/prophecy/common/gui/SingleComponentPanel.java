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

import javax.swing.*;
import java.awt.*;

/**
 * As the name says: a panel with a single component, extending over the whole size of the panel.
 *
 * Takes care of all the annoying repaint-revalidate stuff for you.
 */

public class SingleComponentPanel extends JPanel {
  public SingleComponentPanel() {
    super(new BorderLayout());
  }

  public SingleComponentPanel(Component component) {
    this();
    setComponent(component);
  }

  public void setComponent(Component component) {
    removeAll();
    if (component != null) {
      add(BorderLayout.CENTER, component);
    }
    GUIUtil.revalidateAndRepaint(this);
  }
}